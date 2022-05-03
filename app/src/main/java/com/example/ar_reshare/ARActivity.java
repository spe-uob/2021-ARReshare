package com.example.ar_reshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.Image;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ar_reshare.samplerender.Framebuffer;
import com.example.ar_reshare.samplerender.GLError;
import com.example.ar_reshare.samplerender.Mesh;
import com.example.ar_reshare.samplerender.SampleRender;
import com.example.ar_reshare.samplerender.Shader;
import com.example.ar_reshare.samplerender.Texture;
import com.example.ar_reshare.samplerender.VertexBuffer;
import com.example.ar_reshare.samplerender.arcore.BackgroundRenderer;
import com.example.ar_reshare.samplerender.arcore.PlaneRenderer;
import com.example.ar_reshare.samplerender.arcore.SpecularCubemapFilter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Camera;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.InstantPlacementPoint;
import com.google.ar.core.LightEstimate;
import com.google.ar.core.Plane;
import com.google.ar.core.PointCloud;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.example.ar_reshare.helpers.*;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.NotYetAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ARActivity extends Fragment implements SampleRender.Renderer{

    // See the definition of updateSphericalHarmonicsCoefficients for an explanation of these
    // constants.
    private static final float[] sphericalHarmonicFactors = {
            0.282095f,
            -0.325735f,
            0.325735f,
            -0.325735f,
            0.273137f,
            -0.273137f,
            0.078848f,
            -0.273137f,
            0.136569f,
    };

    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 100f;

    private static final int CUBEMAP_RESOLUTION = 16;
    private static final int CUBEMAP_NUMBER_OF_IMPORTANCE_SAMPLES = 32;
    private static final int MAX_ANCHORED_PRODUCTS = 1;

    // Rendering. The Renderers are created here, and initialized when the GL surface is created.
    private GLSurfaceView surfaceView;

    private boolean installRequested;

    private Session session;
    private final SnackbarHelper messageSnackbarHelper = new SnackbarHelper();
    private DisplayRotationHelper displayRotationHelper;
    private final TrackingStateHelper trackingStateHelper = new TrackingStateHelper(getActivity());
    private SampleRender render;

    private PlaneRenderer planeRenderer;
    private BackgroundRenderer backgroundRenderer;
    private Framebuffer virtualSceneFramebuffer;
    private boolean hasSetTextureNames = false;

    private final DepthSettings depthSettings = new DepthSettings();

    private final InstantPlacementSettings instantPlacementSettings = new InstantPlacementSettings();

    // Point Cloud
    private VertexBuffer pointCloudVertexBuffer;
    private Mesh pointCloudMesh;
    private Shader pointCloudShader;
    // Keep track of the last point cloud rendered to avoid updating the VBO if point cloud
    // was not changed.  Do this using the timestamp since we can't compare PointCloud objects.
    private long lastPointCloudTimestamp = 0;

    // Virtual object (ARCore pawn)
    private Mesh virtualObjectMesh;

    private Shader virtualObjectShader;
    private Texture virtualObjectTexture;

    // Environmental HDR
    private Texture dfgTexture;
    private SpecularCubemapFilter cubemapFilter;

    // Temporary matrix allocated here to reduce number of allocations for each frame.
    private final float[] modelMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] modelViewMatrix = new float[16]; // view x model
    private final float[] modelViewProjectionMatrix = new float[16]; // projection x view x model
    private final float[] sphericalHarmonicsCoefficients = new float[9 * 3];
    private final float[] viewInverseMatrix = new float[16];
    private final float[] worldLightDirection = {0.0f, 0.0f, 0.0f, 0.0f};
    private final float[] viewLightDirection = new float[4]; // view x world light direction

    // The list of products fetched from the backend
    private List<Product> products;
    private CountDownLatch readyLatch;
    private int TIMEOUT_IN_SECONDS = 10;

    // The list of currently displayed Product Objects
    private final Queue<ProductObject> productObjectQueue = new LinkedList<>();
    private static final double DELETE_ANCHOR_ANGLE_BOUNDARY = 15 * Math.PI/180; // degrees

    // The set of currently displayed products
    // This should be combined with productObjects in the future
    private final Set<Product> displayedProducts = new HashSet<>();
    private final Set<Product> currentlyPointedProducts = new HashSet<>();
    private boolean productBoxHidden = true;
    private Map<Product, User> contributorMap = new HashMap<>();

    // Compass object
    private Compass compass;
    // Compass animation
    private double lastCompassButtonAngle = 0;
    private double lastMedianAngle = 0;

    // Used for stabilising compass readings
    private final int MAX_COMPASS_READING_QUEUE_SIZE = 30;
    private double[] compassReadingsArray = new double[MAX_COMPASS_READING_QUEUE_SIZE];
    private int compassReadingSize = 0;
    private int compassReadingIndex = 0;
    private final int COMPASS_POLLING_RATE = 2; // Milliseconds
    private boolean pauseCompass = false;
    private final int COMPASS_MEDIAN_REFRESH_RATE = 30;
    private int compassMedianCountdown = COMPASS_MEDIAN_REFRESH_RATE;

    // Location related attributes:
    // Built-in class which provider current location
    private FusedLocationProviderClient fusedLocationClient;
    // The users last known location
    private Location lastKnownLocation;

    // Map to store the required angle for each product
    private Map<Product, Double> productAngles = new HashMap<>();

    // The acceptable limit of angle offset to product
    private static final double ANGLE_LIMIT = 20 * Math.PI/180; // degrees converted to radians

    // Instructions
    private int instructionProgress = 0;
    private final int INSTRUCTIONS_NUMBER = 5;
    private static boolean instructionsShowing = false;
    private static boolean hideInstructions = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_aractivity, container, false);
        //setContentView(R.layout.activity_aractivity);

        surfaceView = view.findViewById(R.id.surfaceview);
        displayRotationHelper = new DisplayRotationHelper(/*context=*/ getActivity());

        // Set up renderer.
        render = new SampleRender(surfaceView, this, getActivity().getAssets());

        installRequested = false;

        depthSettings.onCreate(getActivity());
        instantPlacementSettings.onCreate(getActivity());

        // Make the AR wait on the following two conditions
        // 1. Device location is ready
        // 2. Products have been received from backend
        // Once these conditions are met the AR can proceed to be populated
        this.readyLatch = new CountDownLatch(2);

        getLatestProducts();

        // Define the onclick event for compass (regenerate) button
        ImageButton regenerate_button = view.findViewById(R.id.regenerate_button);
        regenerate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCompassButtonPressed(v);
            }
        });

        // Define the onclick event for instructions button
        ImageButton instructions_button = view.findViewById(R.id.instructions_button);
        instructions_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInstructions();
            }
        });

        // Start the compass
        new Thread(() -> compass = new Compass(getActivity())).start();

        getDeviceLocation();

        System.out.println("   FINISHED ONCREATE");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroy() {
        if (session != null) {
            // Explicitly close ARCore Session to release native resources.
            // Review the API reference for important considerations before calling close() in apps with
            // more complicated lifecycle requirements:
            // https://developers.google.com/ar/reference/java/arcore/reference/com/google/ar/core/Session#close()
            session.close();
            session = null;
        }

        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (session == null) {
            Exception exception = null;
            String message = null;
            try {
                switch (ArCoreApk.getInstance().requestInstall(getActivity(), !installRequested)) {
                    case INSTALL_REQUESTED:
                        installRequested = true;
                        return;
                    case INSTALLED:
                        break;
                }

                // Create the session.
                session = new Session(/* context= */ getActivity());
            } catch (UnavailableArcoreNotInstalledException
                    | UnavailableUserDeclinedInstallationException e) {
                message = "Please install ARCore";
                exception = e;
            } catch (UnavailableApkTooOldException e) {
                message = "Please update ARCore";
                exception = e;
            } catch (UnavailableSdkTooOldException e) {
                message = "Please update this app";
                exception = e;
            } catch (UnavailableDeviceNotCompatibleException e) {
                message = "This device does not support AR";
                exception = e;
            } catch (Exception e) {
                message = "Failed to create AR session";
                exception = e;
            }

            if (message != null) {
                //messageSnackbarHelper.showError(this, message);
                //Log.e(TAG, "Exception creating session", exception);
                return;
            }
        }

        // Note that order matters - see the note in onPause(), the reverse applies here.
        try {
            configureSession();
            // To record a live camera session for later playback, call
            // `session.startRecording(recordingConfig)` at anytime. To playback a previously recorded AR
            // session instead of using the live camera feed, call
            // `session.setPlaybackDatasetUri(Uri)` before calling `session.resume()`. To
            // learn more about recording and playback, see:
            // https://developers.google.com/ar/develop/java/recording-and-playback
            session.resume();
        } catch (CameraNotAvailableException e) {
            //messageSnackbarHelper.showError(this, "Camera not available. Try restarting the app.");
            session = null;
            return;
        }

        surfaceView.onResume();
        displayRotationHelper.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (session != null) {
            // Note that the order matters - GLSurfaceView is paused first so that it does not try
            // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
            // still call session.update() and get a SessionPausedException.
            displayRotationHelper.onPause();
            surfaceView.onPause();
            session.pause();
            pauseCompass = true;
        }
    }



    //    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
//        super.onRequestPermissionsResult(requestCode, permissions, results);
//        if (!CameraPermissionHelper.hasCameraPermission(getActivity())) {
//            // Use toast instead of snackbar here since the activity will exit.
//            Toast.makeText(getActivity(), "Camera permission is needed to run this application", Toast.LENGTH_LONG)
//                    .show();
//            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(getActivity())) {
//                // Permission denied with checking "Do not ask again".
//                CameraPermissionHelper.launchPermissionSettings(getActivity());
//            }
//            getActivity().finish();
//        }
//        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
//            // If request is cancelled, the grantResults array will be empty
//            if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
//                // Location permission has been granted
//                locationPermissionGranted = true;
//                System.out.println("Location has been granted");
//            } else {
//                // TODO: Explain to user that the feature is unavailable because
//                //  the permissions have not been granted
//            }
//            return;
//        }
//    }

    private void takeCompassReadings() {
        new Thread(() -> {
            while (!pauseCompass) {
                double angle = compass.getAngleToNorth();
                // Stabilise compass reading
                angle = stabiliseCompassReading(angle);
                lastMedianAngle = angle;
                try {
                    Thread.sleep(COMPASS_POLLING_RATE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void showInstructions() {
        if (instructionsShowing) return;

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View instructionsWindow = inflater.inflate(R.layout.instructions_popup, null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        // Allows to tap outside the popup to dismiss it
        boolean focusable = false;

        final PopupWindow popupWindow = new PopupWindow(instructionsWindow, width, height, focusable);

        popupWindow.showAtLocation(instructionsWindow, Gravity.CENTER, 0, 0);
        instructionsShowing = true;

        Button button1 = instructionsWindow.findViewById(R.id.instructionsCancel);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                instructionsShowing = false;
            }
        });

        Button button2 = instructionsWindow.findViewById(R.id.instructionsConfirm);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleInstructionProgress(instructionsWindow, popupWindow);
            }
        });
    }

    private void handleInstructionProgress(View instructionsWindow, PopupWindow popupWindow) {
        TextView messageText = instructionsWindow.findViewById(R.id.beaverMessage);
        Button button = instructionsWindow.findViewById(R.id.instructionsConfirm);
        Button cancelButton = instructionsWindow.findViewById(R.id.instructionsCancel);
        LinearLayout hideSetting = instructionsWindow.findViewById(R.id.hideInstructions);
        CheckBox hideCheckBox = instructionsWindow.findViewById(R.id.hideCheckBox);
        String message = "";
        switch (instructionProgress) {
            case 0:
                message = "Great! First thing to know is that you are in the AR View. Here you can look around you to find local products currently being shared.";
                button.setText("Cool!");
                cancelButton.setText("Exit");
                break;
            case 1:
                message = "Start by taking a step, forwards and backwards, and rotating your phone slowly 360 degrees to scan the floor. You will see the floor surface being detected on the screen.";
                button.setText("Done!");
                break;
            case 2:
                message = "Now, notice there is a big compass at the bottom of your screen which rotates as you move. Press it to start seeing products.";
                break;
            case 3:
                message = "Now, rotate yourself slowly, and notice how products will start appearing in the section above. Press on a product to see more information.";
                button.setText("I can see them!");
                break;
            case 4:
                message = "If you ever forget, these instructions, just click on the information icon, in the bottom right, and I will be back!";
                button.setText("Thanks!");
                hideSetting.setVisibility(View.VISIBLE);
                hideInstructions = hideCheckBox.isChecked();
                hideCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> hideInstructions = isChecked);
                break;
            default:
                hideSetting.setVisibility(View.INVISIBLE);
                popupWindow.dismiss();
                instructionsShowing = false;
        }
        if (instructionProgress < INSTRUCTIONS_NUMBER) instructionProgress += 1;
        else instructionProgress = 0;
        messageText.setText(message);
    }

    private void getLatestProducts() {
        BackendController.searchListings(0, 100, new BackendController.BackendSearchResultCallback() {
            @Override
            public void onBackendSearchResult(boolean success, List<Product> searchResults) {
                if (success) {
                    products = searchResults;
                    readyLatch.countDown();
                }
            }
        });
    }

    private void onCompassButtonPressed(View view) {
        // Rotation animation
        ObjectAnimator.ofFloat(view, "rotation", (float) lastCompassButtonAngle, (float) lastCompassButtonAngle+360).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean success = readyLatch.await(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
                    if (success) {
                        // Get nearby products and calculate required angles
                        resetProductObjects();
                        populateProducts();
                        return;
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Failed to fetch your location or the products from the server. Please ensure you have access to an internet connection.",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onSurfaceCreated(SampleRender render) {
        if (!hideInstructions) getActivity().runOnUiThread(() -> showInstructions());
        takeCompassReadings();

        // Prepare the rendering objects. This involves reading shaders and 3D model files, so may throw
        // an IOException.
        try {
            planeRenderer = new PlaneRenderer(render);
            backgroundRenderer = new BackgroundRenderer(render);
            virtualSceneFramebuffer = new Framebuffer(render, /*width=*/ 1, /*height=*/ 1);

            cubemapFilter =
                    new SpecularCubemapFilter(
                            render, CUBEMAP_RESOLUTION, CUBEMAP_NUMBER_OF_IMPORTANCE_SAMPLES);
            // Load DFG lookup table for environmental lighting
            dfgTexture =
                    new Texture(
                            render,
                            Texture.Target.TEXTURE_2D,
                            Texture.WrapMode.CLAMP_TO_EDGE,
                            /*useMipmaps=*/ false);
            // The dfg.raw file is a raw half-float texture with two channels.
            final int dfgResolution = 64;
            final int dfgChannels = 2;
            final int halfFloatSize = 2;

            ByteBuffer buffer =
                    ByteBuffer.allocateDirect(dfgResolution * dfgResolution * dfgChannels * halfFloatSize);
            try (InputStream is = getActivity().getAssets().open("models/dfg.raw")) {
                is.read(buffer.array());
            }
            // SampleRender abstraction leaks here.
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, dfgTexture.getTextureId());
            GLError.maybeThrowGLException("Failed to bind DFG texture", "glBindTexture");
            GLES30.glTexImage2D(
                    GLES30.GL_TEXTURE_2D,
                    /*level=*/ 0,
                    GLES30.GL_RG16F,
                    /*width=*/ dfgResolution,
                    /*height=*/ dfgResolution,
                    /*border=*/ 0,
                    GLES30.GL_RG,
                    GLES30.GL_HALF_FLOAT,
                    buffer);
            GLError.maybeThrowGLException("Failed to populate DFG texture", "glTexImage2D");

            // Point cloud
            pointCloudShader =
                    Shader.createFromAssets(
                            render, "shaders/point_cloud.vert", "shaders/point_cloud.frag", /*defines=*/ null)
                            .setVec4(
                                    "u_Color", new float[] {31.0f / 255.0f, 188.0f / 255.0f, 210.0f / 255.0f, 1.0f})
                            .setFloat("u_PointSize", 5.0f);
            // four entries per vertex: X, Y, Z, confidence
            pointCloudVertexBuffer =
                    new VertexBuffer(render, /*numberOfEntriesPerVertex=*/ 4, /*entries=*/ null);
            final VertexBuffer[] pointCloudVertexBuffers = {pointCloudVertexBuffer};
            pointCloudMesh =
                    new Mesh(
                            render, Mesh.PrimitiveMode.POINTS, /*indexBuffer=*/ null, pointCloudVertexBuffers);

            virtualObjectMesh = Mesh.createFromAsset(render, "models/pawn.obj");

            // Virtual object to render (ARCore pawn)
            virtualObjectTexture =
                    Texture.createFromAsset(
                            render,
                            "models/electronics_colours.png",
                            Texture.WrapMode.CLAMP_TO_EDGE,
                            Texture.ColorFormat.SRGB);

            virtualObjectShader =
                    Shader.createFromAssets(
                            render,
                            "shaders/environmental_hdr.vert",
                            "shaders/environmental_hdr.frag",
                            /*defines=*/ new HashMap<String, String>() {
                                {
                                    put(
                                            "NUMBER_OF_MIPMAP_LEVELS",
                                            Integer.toString(cubemapFilter.getNumberOfMipmapLevels()));
                                }
                            })
                            .setTexture("u_AlbedoTexture", virtualObjectTexture)
                            .setTexture("u_Cubemap", cubemapFilter.getFilteredCubemapTexture())
                            .setTexture("u_DfgTexture", dfgTexture);
        } catch (IOException e) {
            //Log.e(TAG, "Failed to read a required asset file", e);
            messageSnackbarHelper.showError(getActivity(), "Failed to read a required asset file: " + e);
        }
    }

    @Override
    public void onSurfaceChanged(SampleRender render, int width, int height) {
        displayRotationHelper.onSurfaceChanged(width, height);
        virtualSceneFramebuffer.resize(width, height);
    }

    @Override
    public void onDrawFrame(SampleRender render) {
        if (session == null) {
            return;
        }

        // Texture names should only be set once on a GL thread unless they change. This is done during
        // onDrawFrame rather than onSurfaceCreated since the session is not guaranteed to have been
        // initialized during the execution of onSurfaceCreated.
        if (!hasSetTextureNames) {
            session.setCameraTextureNames(
                    new int[] {backgroundRenderer.getCameraColorTexture().getTextureId()});
            hasSetTextureNames = true;
        }

        // -- Update per-frame state

        // Notify ARCore session that the view size changed so that the perspective matrix and
        // the video background can be properly adjusted.
        displayRotationHelper.updateSessionIfNeeded(session);

        // Obtain the current frame from ARSession. When the configuration is set to
        // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
        // camera framerate.
        Frame frame;
        try {
            frame = session.update();
        } catch (CameraNotAvailableException e) {
            //Log.e(TAG, "Camera not available during onDrawFrame", e);
            //messageSnackbarHelper.showError(getActivity(), "Camera not available. Try restarting the app.");
            return;
        }

        Camera camera = frame.getCamera();

        // Update BackgroundRenderer state to match the depth settings.
        try {
            backgroundRenderer.setUseDepthVisualization(
                    render, depthSettings.depthColorVisualizationEnabled());
            backgroundRenderer.setUseOcclusion(render, depthSettings.useDepthForOcclusion());
        } catch (IOException e) {
            //Log.e(TAG, "Failed to read a required asset file", e);
            //messageSnackbarHelper.showError(getActivity(), "Failed to read a required asset file: " + e);
            return;
        }
        // BackgroundRenderer.updateDisplayGeometry must be called every frame to update the coordinates
        // used to draw the background camera image.
        backgroundRenderer.updateDisplayGeometry(frame);

        if (camera.getTrackingState() == TrackingState.TRACKING
                && (depthSettings.useDepthForOcclusion()
                || depthSettings.depthColorVisualizationEnabled())) {
            try (Image depthImage = frame.acquireDepthImage()) {
                backgroundRenderer.updateCameraDepthTexture(depthImage);
            } catch (NotYetAvailableException e) {
                // This normally means that depth data is not available yet. This is normal so we will not
                // spam the logcat with this.
            }
        }

        // Keep the screen unlocked while tracking, but allow it to lock when tracking stops.
        //trackingStateHelper.updateKeepScreenOnFlag(camera.getTrackingState());

        // MAIN FUNCTIONALITY
        // On each frame update:

        // 1. Get user's angle to the North (Compass)
        //double angle = compass.getAngleToNorth();
        double angle = lastMedianAngle;
        rotateCompass(angle);
        // Stabilise compass reading
        //angle = stabiliseCompassReading(angle);
        //System.out.println("median " + angle*(180/Math.PI) + " degrees to north clockwise");
        float[] pose = camera.getDisplayOrientedPose().getTranslation();
        //System.out.println("x=" + pose[0] + " z=" + pose[2]);

        //detachAnchorIfMoved(angle);

        // 2. Check if user is pointing at a product
        List<Product> pointingProducts = checkIfPointingAtProduct(angle);

        // 3. Spawn a product in front of the user if yes
        if (!pointingProducts.isEmpty()) {
            // If product is not already being displayed, spawn it
            // Note: The product which is closest to the angle will be spawned, hence index zero
            Product closestProduct = pointingProducts.get(0);
            if (!this.displayedProducts.contains(closestProduct)) {
                // Check if ARCore is tracking
                if (camera.getTrackingState() == TrackingState.TRACKING) {
                    spawnProduct(camera, pointingProducts.get(0), angle);
                }
            }
            prepareProductBoxes(pointingProducts);
        } else {
            // Hide product boxes if currently not pointing at any product
            if (!productBoxHidden) {
                resetScrollView(null);
            }

        }


        if (frame.getTimestamp() != 0) {
            // Suppress rendering if the camera did not produce the first frame yet. This is to avoid
            // drawing possible leftover data from previous sessions if the texture is reused.
            backgroundRenderer.drawBackground(render);
        }

        // If not tracking, don't draw 3D objects.
        if (camera.getTrackingState() == TrackingState.PAUSED) {
            return;
        }

        // -- Draw non-occluded virtual objects (planes, point cloud)

        // Get projection matrix.
        camera.getProjectionMatrix(projectionMatrix, 0, Z_NEAR, Z_FAR);

        // Get camera matrix and draw.
        camera.getViewMatrix(viewMatrix, 0);

        // Visualize tracked points.
        // Use try-with-resources to automatically release the point cloud.
        try (PointCloud pointCloud = frame.acquirePointCloud()) {
            if (pointCloud.getTimestamp() > lastPointCloudTimestamp) {
                pointCloudVertexBuffer.set(pointCloud.getPoints());
                lastPointCloudTimestamp = pointCloud.getTimestamp();
            }
            Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
            pointCloudShader.setMat4("u_ModelViewProjection", modelViewProjectionMatrix);
            render.draw(pointCloudMesh, pointCloudShader);
        }

        // Visualize planes.
        planeRenderer.drawPlanes(
                render,
                session.getAllTrackables(Plane.class),
                camera.getDisplayOrientedPose(),
                projectionMatrix);

        // -- Draw occluded virtual objects

        // Update lighting parameters in the shader
        updateLightEstimation(frame.getLightEstimate(), viewMatrix);

        // Visualize anchors created by touch.
        render.clear(virtualSceneFramebuffer, 0f, 0f, 0f, 0f);

        // Iterates through existing anchors and draws them on each frame
        // TODO: Sometimes ConcurrentModificationException is raised when the regenerate button
        //  is pressed and (?) the frame is being drawn, detect the issue and resolve it
        for (ProductObject obj : this.productObjectQueue)  {
            Anchor anchor = obj.getAnchor();
            Trackable trackable = obj.getTrackable();

            // Get the current pose of an Anchor in world space. The Anchor pose is updated
            // during calls to session.update() as ARCore refines its estimate of the world.
            anchor.getPose().toMatrix(modelMatrix, 0);

            // Calculate model/view/projection matrices
            Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
            Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);

            // Update shader properties and draw
            virtualObjectShader.setMat4("u_ModelView", modelViewMatrix);
            virtualObjectShader.setMat4("u_ModelViewProjection", modelViewProjectionMatrix);

            virtualObjectShader.setTexture("u_AlbedoTexture", virtualObjectTexture);

            render.draw(virtualObjectMesh, virtualObjectShader, virtualSceneFramebuffer);
        }

        // Compose the virtual scene with the background.
        backgroundRenderer.drawVirtualScene(render, virtualSceneFramebuffer, Z_NEAR, Z_FAR);
    }


    private Texture getCategoryTexture(Product product) {
        Category category = Category.getCategoryById(product.getCategoryID());
        String categoryTextureFile = "";
        switch (category) {
            case CLOTHING:
                categoryTextureFile = "models/clothing_colours.png";
                break;
            case ACCESSORIES:
                categoryTextureFile = "models/accessory_colours.png";
                break;
            case BOOKS:
                categoryTextureFile = "models/books_colours.png";
                break;
            case ELECTRONICS:
                categoryTextureFile = "models/electronics_colours.png";
                break;
            case HOUSEHOLD:
                categoryTextureFile = "models/household_colours.png";
                break;
            default:
                categoryTextureFile = "models/others_colours.png";
                break;
        }
        Texture texture = null;
        try {
             texture =
                    Texture.createFromAsset(
                            render,
                            categoryTextureFile,
                            Texture.WrapMode.CLAMP_TO_EDGE,
                            Texture.ColorFormat.SRGB);
        } catch (IOException e) {
            System.out.println("EXCEPTION WHEN CREATING TEXTURE " + e);
        }
        return texture;
    }

    /** Checks if we detected at least one plane. */
    private boolean hasTrackingPlane() {
        for (Plane plane : session.getAllTrackables(Plane.class)) {
            if (plane.getTrackingState() == TrackingState.TRACKING) {
                return true;
            }
        }
        return false;
    }

    /** Update state based on the current frame's light estimation. */
    private void updateLightEstimation(LightEstimate lightEstimate, float[] viewMatrix) {
        if (lightEstimate.getState() != LightEstimate.State.VALID) {
            virtualObjectShader.setBool("u_LightEstimateIsValid", false);
            return;
        }
        virtualObjectShader.setBool("u_LightEstimateIsValid", true);

        Matrix.invertM(viewInverseMatrix, 0, viewMatrix, 0);
        virtualObjectShader.setMat4("u_ViewInverse", viewInverseMatrix);

        updateMainLight(
                lightEstimate.getEnvironmentalHdrMainLightDirection(),
                lightEstimate.getEnvironmentalHdrMainLightIntensity(),
                viewMatrix);
        updateSphericalHarmonicsCoefficients(
                lightEstimate.getEnvironmentalHdrAmbientSphericalHarmonics());
        cubemapFilter.update(lightEstimate.acquireEnvironmentalHdrCubeMap());
    }

    private void updateMainLight(float[] direction, float[] intensity, float[] viewMatrix) {
        // We need the direction in a vec4 with 0.0 as the final component to transform it to view space
        worldLightDirection[0] = direction[0];
        worldLightDirection[1] = direction[1];
        worldLightDirection[2] = direction[2];
        Matrix.multiplyMV(viewLightDirection, 0, viewMatrix, 0, worldLightDirection, 0);
        virtualObjectShader.setVec4("u_ViewLightDirection", viewLightDirection);
        virtualObjectShader.setVec3("u_LightIntensity", intensity);
    }

    private void updateSphericalHarmonicsCoefficients(float[] coefficients) {
        // Pre-multiply the spherical harmonics coefficients before passing them to the shader. The
        // constants in sphericalHarmonicFactors were derived from three terms:
        //
        // 1. The normalized spherical harmonics basis functions (y_lm)
        //
        // 2. The lambertian diffuse BRDF factor (1/pi)
        //
        // 3. A <cos> convolution. This is done to so that the resulting function outputs the irradiance
        // of all incoming light over a hemisphere for a given surface normal, which is what the shader
        // (environmental_hdr.frag) expects.
        //
        // You can read more details about the math here:
        // https://google.github.io/filament/Filament.html#annex/sphericalharmonics

        if (coefficients.length != 9 * 3) {
            throw new IllegalArgumentException(
                    "The given coefficients array must be of length 27 (3 components per 9 coefficients");
        }

        // Apply each factor to every component of each coefficient
        for (int i = 0; i < 9 * 3; ++i) {
            sphericalHarmonicsCoefficients[i] = coefficients[i] * sphericalHarmonicFactors[i / 3];
        }
        virtualObjectShader.setVec3Array(
                "u_SphericalHarmonicsCoefficients", sphericalHarmonicsCoefficients);
    }

    /** Configures the session with feature settings. */
    private void configureSession() {
        Config config = session.getConfig();
        config.setLightEstimationMode(Config.LightEstimationMode.ENVIRONMENTAL_HDR);
        if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
            config.setDepthMode(Config.DepthMode.AUTOMATIC);
        } else {
            config.setDepthMode(Config.DepthMode.DISABLED);
        }
        if (instantPlacementSettings.isInstantPlacementEnabled()) {
            config.setInstantPlacementMode(Config.InstantPlacementMode.LOCAL_Y_UP);
        } else {
            config.setInstantPlacementMode(Config.InstantPlacementMode.DISABLED);
        }
        session.configure(config);
    }

    // TODO: Spawn products again
    // If it is concluded that a user is currently pointing at a product, and a product should
    // be spawned, pass the camera, the relevant product and the current angle to north to this
    // method to spawn a product in a virtual space
    private void spawnProduct(Camera camera, Product product, double angleToNorth) {
        System.out.println("    SPAWN PRODUCT CALLED    ");
        float distance = 1.5f; // metres away
        ProductObject displayedInAR = this.productObjectQueue.peek();
        // Only spawn products, if none are displayed or the displayed one is different
        if (this.productObjectQueue.size() == 0 || displayedInAR.getProduct() != product) {
            // Current position of the user
            Pose cameraPose = camera.getDisplayOrientedPose();

            float[] coords = cameraPose.getTranslation();

            // Get new coordinates set distance in meters in front of the user
            // Using Trigonometry

            angleToNorth = angleToNorth % Math.PI;

            float oppositeDelta = (float) (Math.sin(angleToNorth) * distance);
            float adjacentDelta = (float) (Math.cos(angleToNorth) * distance);

            System.out.println("ANCHOR ANGLE" + angleToNorth);

            float deltaX;
            float deltaZ;

            if (angleToNorth < Math.PI/2) {
                deltaX = oppositeDelta;
                deltaZ = - adjacentDelta;
            } else if (angleToNorth >= Math.PI/2 && angleToNorth < Math.PI) {
                deltaX = adjacentDelta;
                deltaZ = oppositeDelta;
            } else if (angleToNorth >= Math.PI && angleToNorth < Math.PI*3/2) {
                deltaX = - oppositeDelta;
                deltaZ = adjacentDelta;
            } else {
                deltaX = - adjacentDelta;
                deltaZ = - oppositeDelta;
            }

            float[] objectCoords = new float[]{coords[0] + deltaX, coords[1], coords[2] + deltaZ};
            Pose anchorPose = new Pose(objectCoords, new float[]{0, 0, 0, 0});

            System.out.println("ANCHOR CAMERA POSE = " + cameraPose);
            System.out.println("NEW ANCHOR POSE = " + anchorPose);
            System.out.println("ANCHOR ANGLE = " + angleToNorth);

            System.out.println(" ANCHORS PRESENT " + session.getAllAnchors().size());
            System.out.println(" ANCHORS TRACKABLES PRESENT " + session.getAllTrackables(Plane.class).size());
            System.out.println(" ANCHORS QUEUE SIZE " + this.productObjectQueue.size());

            // If exceeded limit of tracked anchors, replace last one
            if (this.productObjectQueue.size() == MAX_ANCHORED_PRODUCTS) {
                ProductObject oldest = this.productObjectQueue.poll();
                oldest.getAnchor().detach();
                this.displayedProducts.remove(oldest.getProduct());
            }

            // Try to create an anchor catching any exceptions
            try {
                // An anchor can only be created if the state is tracked
                if (camera.getTrackingState() == TrackingState.TRACKING) {
                    Anchor newAnchor = session.createAnchor(anchorPose);
                    ProductObject newObject = new ProductObject(newAnchor, null, product);
                    this.productObjectQueue.add(newObject);
                    this.displayedProducts.add(product);
                    System.out.println("ANCHOR PRODUCT SPAWNED = " + product.getName());
                    System.out.println("ANCHOR PRODUCT CATEGORY = " + product.getCategoryID());
                }
            } catch (Exception e) {
                System.out.println("FAILED TO CREATE AN ACHOR");
            }
        }
    }

    // If user rotates by the boundary value, detach the anchor
    private void detachAnchorIfMoved(double angle) {
        if (this.productObjectQueue.size() > 0) {
            ProductObject oldest = this.productObjectQueue.peek();
            double requiredAngle = this.productAngles.get(oldest.getProduct());
            if (Math.abs(requiredAngle - angle) > DELETE_ANCHOR_ANGLE_BOUNDARY) {
                System.out.println("DETACH ANCHOR");
                oldest.getAnchor().detach();
                this.displayedProducts.remove(oldest.getProduct());
                this.productObjectQueue.clear();
            }
        }
    }

    // Get the most recent location of the device
    private void getDeviceLocation() {
        SwipeActivity parent = (SwipeActivity) getActivity();
        try {
            if (parent.locationPermissionGranted) {
                parent.fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    lastKnownLocation = location;
                                    readyLatch.countDown();
                                }
                            }
                        });
            }
        } catch (SecurityException e)  {
            // TODO: Implement appropriate error catching
            System.out.println("   FAIL LOCATION EXCEPTION");
            System.out.println(e);
        }
    }

    // Prepare products for display by finding the required angle for each product
    private void populateProducts() {
        for (Product product : this.products) {
            Location productLocation = new Location("ManualProvider");
            productLocation.setLatitude(product.getCoordinates().latitude);
            productLocation.setLongitude(product.getCoordinates().longitude);
            double requiredAngle = lastKnownLocation.bearingTo(productLocation);
            requiredAngle = requiredAngle * Math.PI/180;
            if (requiredAngle < 0) requiredAngle = 2*Math.PI + requiredAngle;
            //System.out.println("required angle = " + requiredAngle);
            productAngles.put(product, requiredAngle);
        }
    }

    // Reset anchors in the AR space
    private void resetProductObjects() {
        for (ProductObject productObject : productObjectQueue) {
            productObject.getAnchor().detach();
        }
        displayedProducts.removeAll(productAngles.keySet());
        contributorMap.clear();
        productObjectQueue.clear();
        resetScrollView(null);
    }

    private void resetScrollView(CountDownLatch latch) {
        getActivity().runOnUiThread(() -> {
            currentlyPointedProducts.clear();
            LinearLayout scrollView = getActivity().findViewById(R.id.ARScrollLayout);
            scrollView.removeAllViewsInLayout();
            TextView textView = getActivity().findViewById(R.id.productsFoundText);
            textView.setText("No products found");
            if (latch != null) latch.countDown();
        });
    }

    // Returns a product if the user is currently pointing at it
    private List<Product> checkIfPointingAtProduct(double userAngle) {
        // A map of products being pointed at and the angle difference
        //Map<Product, Double> pointingAt = new HashMap<>();
        List<Product> testList = new ArrayList<>();
        for (Map.Entry<Product, Double> productAnglePair : productAngles.entrySet()) {
            double angleDiff = Math.abs(userAngle - productAnglePair.getValue());

            if (angleDiff <= ANGLE_LIMIT) {
                //pointingAt.put(productAnglePair.getKey(), angleDiff);
                testList.add(productAnglePair.getKey());
            }
        }

//        List<Product> closestProducts = pointingAt.entrySet()
//                .stream()
//                .sorted(Comparator.comparingDouble(pair -> pair.getValue()))
//                .limit(3)
//                .map(pair -> pair.getKey())
//                .collect(Collectors.toList());

        //return closestProducts;
        return testList;
    }

    // Sends a request to the backend to get download the contributor of the product
    private void prepareProductBox(Product product, CountDownLatch latch) {
        // This optimisation means that the user will be downloaded only once
        // The assumption made is that the contributor of a product will never change
        if (this.contributorMap.containsKey(product)) {
            latch.countDown();
        } else {
            BackendController.getProfileByID(0, 1, product.getContributorID(), new BackendController.BackendProfileResultCallback() {
                @Override
                public void onBackendProfileResult(boolean success, User userProfile) {
                    contributorMap.put(product, userProfile);
                    latch.countDown();
                }
            });
        }
    }

    private List<Product> setDifference(List<Product> products) {
        List<Product> toAdd = new ArrayList<>();
        Set<Product> toRemove = new HashSet<>(this.currentlyPointedProducts);
        for (Product product : products) {
            if (!this.currentlyPointedProducts.contains(product)) {
                toAdd.add(product);
            } else {
                toRemove.remove(product);
            }
        }
        return toAdd;
    }


    private void prepareProductBoxes(List<Product> products) {
        LinearLayout scrollView = getActivity().findViewById(R.id.ARScrollLayout);
        final List<Product> allProducts = products;

        // If all products are already displayed skip
        if (this.currentlyPointedProducts.containsAll(products) &&
                this.currentlyPointedProducts.size() == products.size()) return;
        else {
            // If more items need to be added but no items need to be removed
            if (products.containsAll(this.currentlyPointedProducts)) {
                products = setDifference(products);
            } else {
                // Request reset of products and wait until complete
                CountDownLatch latch = new CountDownLatch(1);
                resetScrollView(latch);
                try {
                    latch.await();
                } catch (InterruptedException e) {}
            }
        }
        List<Product> finalProducts = products;

        new Thread(() -> {
            CountDownLatch latch = new CountDownLatch(finalProducts.size());
            for (Product product : finalProducts) {
                prepareProductBox(product, latch);
            }
            try {
                latch.await();

                // Reset the viewed products and proceed to show new products
                for (Product product : finalProducts) {
                    if (this.currentlyPointedProducts.contains(product)) continue;
                    renderProductBox(product, contributorMap.get(product), scrollView);
                    this.currentlyPointedProducts.add(product);
                }

                getActivity().runOnUiThread(() -> {
                    TextView textView = getActivity().findViewById(R.id.productsFoundText);
                    textView.setText(this.currentlyPointedProducts.size() + " product(s) found");
                });
            } catch (InterruptedException e) {}
        }).start();
    }

    // Given a product, render and display a product box
    private void renderProductBox(Product product, User user, LinearLayout scrollView) {
        // runOnUiThread must be called because Android requires changes to UI to be done only by
        // the original thread that created the view hierarchy
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Make Product Box Visible
                LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());
                View productBoxParent = inflater.inflate(R.layout.ar_product_box, scrollView, false);

                View productBox = productBoxParent.findViewById(R.id.productBoxAR);
                productBox.setVisibility(View.VISIBLE);

                // Set parameters depending on product
                TextView title = (TextView) productBox.findViewById(R.id.title);
                title.setText(product.getName());
                TextView contributor = (TextView) productBox.findViewById(R.id.contributor);
                if (user != null) contributor.setText(user.getName());
                else contributor.setText("AR-Reshare user");
                ImageView photo = (ImageView) productBox.findViewById(R.id.productimage);
                Bitmap productPhoto = product.getMainPic();
                if (productPhoto != null) photo.setImageBitmap(product.getMainPic());
                else photo.setImageResource(R.drawable.example_cup);

                // TODO: Add checking for null product location
                // Find and display distance to product
                Location productLocation = new Location("ManualProvider");
                productLocation.setLatitude(product.getCoordinates().latitude);
                productLocation.setLongitude(product.getCoordinates().longitude);
                float dist = lastKnownLocation.distanceTo(productLocation);
                TextView distanceAway = (TextView) productBox.findViewById(R.id.distanceAway);
                distanceAway.setText(Math.round(dist) + " metres away");

                // Link the Product Box to the Product Page
                productBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("contributorID",product.getContributorID());
                        bundle.putString("productName",product.getName());
                        bundle.putString("productDescription",product.getDescription());
                        bundle.putInt("productID",product.getId());
                        bundle.putDouble("lat", product.getCoordinates().latitude);
                        bundle.putDouble("lng",product.getCoordinates().longitude);
                        bundle.putString("postcode",product.getPostcode());
                        bundle.putBoolean("isSaved", product.isSavedByUser());
                        ProductPageActivity productFragment = new ProductPageActivity();
                        productFragment.setArguments(bundle);
                        productFragment.setIsFromFeed(false);
                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_wrapper,productFragment).addToBackStack(null).commit();
                    }
                });
                scrollView.addView(productBoxParent);
                System.out.println(" Added to list");
            }
        });
        productBoxHidden = false;
    }

    // Takes a median of the last set of compass readings to filter out anomalies
    // Self-developed efficient median algorithm
    private double stabiliseCompassReading(double reading) {
        if (true) return reading;
        if (compassReadingSize < MAX_COMPASS_READING_QUEUE_SIZE) {
            compassReadingSize += 1;
        }

        // Insert new reading
        compassReadingsArray[compassReadingIndex] = reading;

        double median = lastMedianAngle;
        if (compassMedianCountdown <= 0) {
            // Find the middle index of the array
            int mid = Math.floorDiv(compassReadingSize, 2);
            int skipCount; // Number of elements to skip
            if (mid > 0) skipCount = mid-1;
            else skipCount = 0;

            median = Arrays.stream(compassReadingsArray).sorted().skip(skipCount).findFirst().getAsDouble();

            compassMedianCountdown = COMPASS_MEDIAN_REFRESH_RATE;
        } else {
            compassMedianCountdown -= 1;
        }

        // Reset index to zero when end of array has been reached
        if (compassReadingIndex == MAX_COMPASS_READING_QUEUE_SIZE-1) {
            compassReadingIndex = 0;
        } else {
            compassReadingIndex += 1;
        }

        return median;
    }

    // Rotates compass to the specified angle to the northß
    private void rotateCompass(double angle) {
        // Convert angle from radians to degrees
        float angleDeg = (float) (angle * 180/Math.PI);

        // The compass must rotate in the opposite direction to the direction of actual rotation
        angleDeg = 360 - angleDeg;

        // Prevent jumping compass animation
        if (angleDeg > 340 && lastCompassButtonAngle < 20) {
            angleDeg = - (360 - angleDeg);
        }

        float finalAngleDeg = angleDeg;

        getActivity().runOnUiThread(() -> {
            View compassButton = getActivity().findViewById(R.id.regenerate_button);
            ObjectAnimator.ofFloat(compassButton, "rotation", (float) lastCompassButtonAngle, finalAngleDeg).start();
            lastCompassButtonAngle = finalAngleDeg;
        });
    }

}

// A class to represent the objects in AR showing the direction to products
class ProductObject {
    private Anchor anchor;
    private Trackable trackable;
    private Product product;

    public ProductObject(Anchor anchor, Trackable trackable, Product product) {
        this.anchor = anchor;
        this.trackable = trackable;
        this.product = product;
    }

    public Anchor getAnchor() {
        return anchor;
    }

    public Trackable getTrackable() {
        return trackable;
    }

    public Product getProduct() { return product; }
}