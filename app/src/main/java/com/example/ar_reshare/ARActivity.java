package com.example.ar_reshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Image;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.android.gms.location.LocationServices;
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
import com.google.ar.core.TrackingFailureReason;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ARActivity extends AppCompatActivity implements SampleRender.Renderer {

    private static final String SEARCHING_PLANE_MESSAGE = "Searching for surfaces...";
    private static final String USER_MOVED_MESSAGE = "You have left your origin. Please regenerate.";

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

    // Rendering. The Renderers are created here, and initialized when the GL surface is created.
    private GLSurfaceView surfaceView;

    private boolean installRequested;

    private Session session;
    private final SnackbarHelper messageSnackbarHelper = new SnackbarHelper();
    private DisplayRotationHelper displayRotationHelper;
    private final TrackingStateHelper trackingStateHelper = new TrackingStateHelper(this);
    private TapHelper tapHelper;
    private SampleRender render;

    private PlaneRenderer planeRenderer;
    private BackgroundRenderer backgroundRenderer;
    private Framebuffer virtualSceneFramebuffer;
    private boolean hasSetTextureNames = false;

    private final DepthSettings depthSettings = new DepthSettings();
    private boolean[] depthSettingsMenuDialogCheckboxes = new boolean[2];

    private final InstantPlacementSettings instantPlacementSettings = new InstantPlacementSettings();
    private boolean[] instantPlacementSettingsMenuDialogCheckboxes = new boolean[1];
    // Assumed distance from the device camera to the surface on which user will try to place objects.
    // This value affects the apparent scale of objects while the tracking method of the
    // Instant Placement point is SCREENSPACE_WITH_APPROXIMATE_DISTANCE.
    // Values in the [0.2, 2.0] meter range are a good choice for most AR experiences. Use lower
    // values for AR experiences where users are expected to place objects on surfaces close to the
    // camera. Use larger values for experiences where the user will likely be standing and trying to
    // place an object on the ground or floor in front of them.
    private static final float APPROXIMATE_DISTANCE_METERS = 2.0f;

    // Point Cloud
    private VertexBuffer pointCloudVertexBuffer;
    private Mesh pointCloudMesh;
    private Shader pointCloudShader;
    // Keep track of the last point cloud rendered to avoid updating the VBO if point cloud
    // was not changed.  Do this using the timestamp since we can't compare PointCloud objects.
    private long lastPointCloudTimestamp = 0;

    // Virtual object (ARCore pawn)
    private Mesh virtualObjectMesh;

    private Mesh objectHat;
    private Mesh objectPhone;
    private Mesh objectBurger;
    private Mesh objectCup;

    private Shader virtualObjectShader;
    private Texture virtualObjectAlbedoTexture;
    private Texture virtualObjectAlbedoInstantPlacementTexture;
    private Texture virtualObjectPbrTexture;

    private Texture burgerTexture;
    private Texture hatTexture;
    private Texture phoneTexture;
    private Texture cupTexture;
    private Shader burgerShader;
    private Shader hatShader;
    private Shader phoneShader;
    private Shader cupShader;


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


    // The list of currently displayed Product Objects
    private final List<ProductObject> productObjects = new ArrayList<>();

    // The set of currently displayed products
    // This should be combined with productObjects in the future
    private final Set<Product> displayedProducts = new HashSet<>();
    private boolean productBoxHidden = true;
    private Product productBoxProduct;

    // Compass object
    private Compass compass;
    // Compass animation
    private double lastCompassButtonAngle = 0;

    // Location related attributes:
    // Built-in class which provider current location
    private FusedLocationProviderClient fusedLocationClient;
    // The users last known location
    private Location lastKnownLocation;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // Map to store the required angle for each product
    private Map<Product, Double> productAngles = new HashMap<>();

    // The acceptable limit of angle offset to product
    private static final double ANGLE_LIMIT = 20 * Math.PI/180; // degrees converted to radians

    // Swiping
    private float x1, x2, y1, y2;
    private final int TOUCH_OFFSET = 100;
    private final int TAP_OFFSET = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aractivity);

        surfaceView = findViewById(R.id.surfaceview);
        displayRotationHelper = new DisplayRotationHelper(/*context=*/ this);

        // Set up renderer.
        render = new SampleRender(surfaceView, this, getAssets());
        System.out.println(render.toString());

        installRequested = false;

        depthSettings.onCreate(this);
        instantPlacementSettings.onCreate(this);

        // Define the onclick event for compass (regenerate) button
        ImageButton regenerate_button = findViewById(R.id.regenerate_button);
        regenerate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get nearby products and calculate required angles
                resetProductObjects();
                populateProducts();

                // Rotation animation
                ObjectAnimator.ofFloat(v, "rotation", (float) lastCompassButtonAngle, (float) lastCompassButtonAngle+360).start();
            }
        });

        // Start the compass
        compass = new Compass(this);

        // Request location permissions if needed and get latest location
        getLocationPermission();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getDeviceLocation();
    }

    @Override
    protected void onDestroy() {
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
    protected void onResume() {
        super.onResume();

        if (session == null) {
            Exception exception = null;
            String message = null;
            try {
                switch (ArCoreApk.getInstance().requestInstall(this, !installRequested)) {
                    case INSTALL_REQUESTED:
                        installRequested = true;
                        return;
                    case INSTALLED:
                        break;
                }

                // ARCore requires camera permissions to operate. If we did not yet obtain runtime
                // permission on Android M and above, now is a good time to ask the user for it.
                if (!CameraPermissionHelper.hasCameraPermission(this)) {
                    CameraPermissionHelper.requestCameraPermission(this);
                    return;
                }

                // Create the session.
                session = new Session(/* context= */ this);
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
                messageSnackbarHelper.showError(this, message);
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
            messageSnackbarHelper.showError(this, "Camera not available. Try restarting the app.");
            session = null;
            return;
        }

        surfaceView.onResume();
        displayRotationHelper.onResume();
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
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            // Use toast instead of snackbar here since the activity will exit.
            Toast.makeText(this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                    .show();
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(this);
            }
            finish();
        }
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the grantResults array will be empty
            if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission has been granted
                locationPermissionGranted = true;
                System.out.println("Location has been granted");
            } else {
                // TODO: Explain to user that the feature is unavailable because
                //  the permissions have not been granted
            }
            return;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus);
    }

    @Override
    public void onSurfaceCreated(SampleRender render) {
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
            try (InputStream is = getAssets().open("models/dfg.raw")) {
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

            // Virtual object to render (ARCore pawn)
//            virtualObjectAlbedoTexture =
//                    Texture.createFromAsset(
//                            render,
//                            "models/pink.png",
//                            Texture.WrapMode.CLAMP_TO_EDGE,
//                            Texture.ColorFormat.SRGB);
            virtualObjectAlbedoInstantPlacementTexture =
                    Texture.createFromAsset(
                            render,
                            "models/grey.png",
                            Texture.WrapMode.CLAMP_TO_EDGE,
                            Texture.ColorFormat.SRGB);
            virtualObjectPbrTexture =
                    Texture.createFromAsset(
                            render,
                            "models/grey.png",
                            Texture.WrapMode.CLAMP_TO_EDGE,
                            Texture.ColorFormat.LINEAR);

            virtualObjectMesh = Mesh.createFromAsset(render, "models/pawn.obj");
            objectHat = Mesh.createFromAsset(render, "models/hat.obj");
            hatShader = setObjectShader();
            hatTexture = setObjectTexture("models/purple.png");
            objectPhone = Mesh.createFromAsset(render, "models/phone.obj");
            phoneShader = setObjectShader();
            phoneTexture = setObjectTexture("models/grey.png");
            objectBurger = Mesh.createFromAsset(render, "models/burger.obj");
            burgerShader = setObjectShader();
            burgerTexture = setObjectTexture("models/burger.png");
            objectCup = Mesh.createFromAsset(render, "models/cup.obj");
            cupShader = setObjectShader();
            cupTexture = setObjectTexture("models/pink.png");
//            virtualObjectShader =
//                    Shader.createFromAssets(
//                            render,
//                            "shaders/environmental_hdr.vert",
//                            "shaders/environmental_hdr.frag",
//                            /*defines=*/ new HashMap<String, String>() {
//                                {
//                                    put(
//                                            "NUMBER_OF_MIPMAP_LEVELS",
//                                            Integer.toString(cubemapFilter.getNumberOfMipmapLevels()));
//                                }
//                            })
//                            .setTexture("u_AlbedoTexture", virtualObjectAlbedoTexture)
//                            .setTexture("u_RoughnessMetallicAmbientOcclusionTexture", virtualObjectPbrTexture)
//                            .setTexture("u_Cubemap", cubemapFilter.getFilteredCubemapTexture())
//                            .setTexture("u_DfgTexture", dfgTexture);
            virtualObjectShader = hatShader; // default shader
        } catch (IOException e) {
            //Log.e(TAG, "Failed to read a required asset file", e);
            messageSnackbarHelper.showError(this, "Failed to read a required asset file: " + e);
        }
    }

    public Texture setObjectTexture(String textureLocation){
        Texture texture = null;
        try {
            texture =
            Texture.createFromAsset(
                    render,
                    textureLocation,
                    Texture.WrapMode.CLAMP_TO_EDGE,
                    Texture.ColorFormat.SRGB);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return texture;
    }

    public Shader setObjectShader(){
        Shader shader = null;
        try {
            shader =
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
                            .setTexture("u_AlbedoTexture", virtualObjectAlbedoTexture)
                            .setTexture("u_RoughnessMetallicAmbientOcclusionTexture", virtualObjectPbrTexture)
                            .setTexture("u_Cubemap", cubemapFilter.getFilteredCubemapTexture())
                            .setTexture("u_DfgTexture", dfgTexture);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return shader;
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
            messageSnackbarHelper.showError(this, "Camera not available. Try restarting the app.");
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
            messageSnackbarHelper.showError(this, "Failed to read a required asset file: " + e);
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

        // MAIN FUNCTIONALITY
        // On each frame update:
        // TODO 0. Check user has not moved -> Reset objects if needed

        // 1. Get user's angle to the North (Compass)
        double angle = compass.getAngleToNorth();

        // 2. Check if user is pointing at a product
        Optional<Product> pointingAt = checkIfPointingAtProduct(angle);

        // 3. Spawn a product in front of the user if yes
        if (pointingAt.isPresent()) {
            // If product is not already being displayed, spawn it
            if (!this.displayedProducts.contains(pointingAt.get())) {
                spawnProduct(camera, pointingAt.get(), angle);
                // When ProductObject has been created, remove this product from the Set
                this.displayedProducts.add(pointingAt.get());
                renderProductBox(pointingAt.get());
                rotateCompass(angle);
            }
            // Else if the product is displayed, but the product box not, display it
            else if (productBoxHidden && this.displayedProducts.contains(pointingAt.get())) {
                renderProductBox(pointingAt.get());
                rotateCompass(angle);
            }
            // Else if the product box is displayed, but is showing other product's information, update it
            else if (productBoxProduct != pointingAt.get() && this.displayedProducts.contains(pointingAt.get())) {
                renderProductBox(pointingAt.get());
                rotateCompass(angle);
            }
        } else {
            // Hide product box if currently not pointing at any product
            if (!productBoxHidden) {
                hideProductBox();
                rotateCompass(angle);
            }

        }

        // Keep the screen unlocked while tracking, but allow it to lock when tracking stops.
        trackingStateHelper.updateKeepScreenOnFlag(camera.getTrackingState());

        // Show a message based on whether tracking has failed, if planes are detected, and if the user
        // has placed any objects.
        String message = null;
        if (camera.getTrackingState() == TrackingState.PAUSED) {
            if (camera.getTrackingFailureReason() == TrackingFailureReason.NONE) {
                message = SEARCHING_PLANE_MESSAGE;
            } else {
                message = TrackingStateHelper.getTrackingFailureReasonString(camera);
            }
        } else if (hasTrackingPlane()) {
        } else {
            message = SEARCHING_PLANE_MESSAGE;
        }
        if (message == null) {
            messageSnackbarHelper.hide(this);
        } else {
            messageSnackbarHelper.showMessage(this, message);
        }

        // -- Draw background

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
        for (ProductObject obj : this.productObjects)  {
            Anchor anchor = obj.getAnchor();
            Trackable trackable = obj.getTrackable();

            // Check object's category
            Category objCategory = obj.getProduct().getCategory();
            if (objCategory.equals(Category.CLOTHING)){
                virtualObjectMesh = objectHat;
                virtualObjectShader = hatShader;
                virtualObjectAlbedoTexture = hatTexture;
            }else if(objCategory.equals(Category.OTHER)){
                virtualObjectMesh = objectCup;
                virtualObjectShader = cupShader;
                virtualObjectAlbedoTexture = cupTexture;
            }else if(objCategory.equals(Category.ELECTRONICS)){
                virtualObjectMesh = objectPhone;
                virtualObjectShader = phoneShader;
                virtualObjectAlbedoTexture = phoneTexture;
            } else {
                virtualObjectMesh = objectBurger;
                virtualObjectShader = burgerShader;
                virtualObjectAlbedoTexture = burgerTexture;
            }

            // Get the current pose of an Anchor in world space. The Anchor pose is updated
            // during calls to session.update() as ARCore refines its estimate of the world.
            anchor.getPose().toMatrix(modelMatrix, 0);

            // Calculate model/view/projection matrices
            Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
            Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);

            // Update shader properties and draw
            virtualObjectShader.setMat4("u_ModelView", modelViewMatrix);
            virtualObjectShader.setMat4("u_ModelViewProjection", modelViewProjectionMatrix);

            if (trackable instanceof InstantPlacementPoint
                    && ((InstantPlacementPoint) trackable).getTrackingMethod()
                    == InstantPlacementPoint.TrackingMethod.SCREENSPACE_WITH_APPROXIMATE_DISTANCE) {
                virtualObjectShader.setTexture(
                        "u_AlbedoTexture", virtualObjectAlbedoInstantPlacementTexture);
            } else {
                virtualObjectShader.setTexture("u_AlbedoTexture", virtualObjectAlbedoTexture);
            }

            render.draw(virtualObjectMesh, virtualObjectShader, virtualSceneFramebuffer);
        }

        // Compose the virtual scene with the background.
        backgroundRenderer.drawVirtualScene(render, virtualSceneFramebuffer, Z_NEAR, Z_FAR);
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

    // If it is concluded that a user is currently pointing at a product, and a product should
    // be spawned, pass the camera, the relevant product and the current angle to north to this
    // method to spawn a product in a virtual space
    private void spawnProduct(Camera camera, Product product, double angleToNorth) {
        if (true) {
            // If the angle (in radians) is negative convert to positive
            if (angleToNorth < 0) {
                angleToNorth = (angleToNorth * -1) + Math.PI;
            }

            // Current position of the user
            Pose cameraPose = camera.getDisplayOrientedPose();
            float[] coords = cameraPose.getTranslation();

            // Get new coordinates two meters in front of the user
            // Using Trigonometry
            double distance = 2; // 2 metres away
            float deltaX = (float) (Math.sin(angleToNorth) * distance);
            float deltaZ = (float) (Math.cos(angleToNorth) * distance);
            float[] objectCoords = new float[]{coords[0] + deltaX, coords[1], coords[2] + deltaZ};
            Pose anchorPose = new Pose(objectCoords, new float[]{0, 0, 0, 0});

            // Create an anchor and a ProductObject associated with it
            Anchor newAnchor = session.createAnchor(anchorPose);
            ProductObject newObject = new ProductObject(newAnchor, null, product);
            this.productObjects.add(newObject);
        }
    }

    // Request location permissions from the device. We will receive a callback
    // to onRequestPermissionsResult with the results.
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Location permission has already been granted previously
            locationPermissionGranted = true;
        } else if (shouldShowRequestPermissionRationale("FINE_LOCATION")) {
            // TODO: Explain to the user why the location permission is needed
        } else {
            // If the location permission has not been granted already,
            // open a window requesting this permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    // Get the most recent location of the device
    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    lastKnownLocation = location;
                                }
                            }
                        });
            }
        } catch (SecurityException e)  {
            // TODO: Implement appropriate error catching
        }
    }

    // Prepare products for display by finding the required angle for each product
    private void populateProducts() {
        List<Product> products = ExampleData.getProducts();
        for (Product product : products) {
            Location productLocation = new Location("ManualProvider");
            productLocation.setLatitude(product.getLocation().latitude);
            productLocation.setLongitude(product.getLocation().longitude);
            double requiredAngle = lastKnownLocation.bearingTo(productLocation);
            requiredAngle = requiredAngle * Math.PI/180;
            productAngles.put(product, requiredAngle);
        }
    }

    // Reset anchors in the AR space
    private void resetProductObjects() {
        for (ProductObject productObject : productObjects) {
            productObject.getAnchor().detach();
        }
        displayedProducts.removeAll(productAngles.keySet());
        int n = productObjects.size();
        for (int i = 0; i < n; i++) {
            productObjects.remove(0);
        }

    }

    // Returns a product if the user is currently pointing at it
    private Optional<Product> checkIfPointingAtProduct(double userAngle) {
        Map.Entry<Product, Double> closestPair = null;
        for (Map.Entry<Product, Double> productAnglePair : productAngles.entrySet()) {
            double angleDiff = Math.abs(userAngle - productAnglePair.getValue());
            if (angleDiff <= ANGLE_LIMIT) {
                if (closestPair == null) closestPair = productAnglePair;
                else {
                    // If two products are close to each other, choose the closest angle
                    double originalDiff = Math.abs(userAngle - closestPair.getValue());
                    if (originalDiff > angleDiff) closestPair = productAnglePair;
                }
            }
        }
        Optional<Product> target;
        if (closestPair != null ) {
            target = Optional.of(closestPair.getKey());
        }
        else {
            target = Optional.empty();
        }
        return target;
    }

    // Given a product, render and display a product box
    private void renderProductBox(Product product) {
        // runOnUiThread must be called because Android requires changes to UI to be done only by
        // the original thread that created the view hierarchy
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Make Product Box Visible
                View productBox = findViewById(R.id.productBoxAR);
                productBox.setVisibility(View.VISIBLE);

                // Set parameters depending on product
                TextView title = (TextView) productBox.findViewById(R.id.title);
                title.setText(product.getName());
                TextView contributor = (TextView) productBox.findViewById(R.id.contributor);
                contributor.setText(product.getContributor().getName());
                ImageView photo = (ImageView) productBox.findViewById(R.id.productimage);
                List<Integer> productPhotos = product.getImages();
                if (productPhotos.size() >= 1) {
                    photo.setImageResource(productPhotos.get(0));
                }

                // Find and display distance to product
                Location productLocation = new Location("ManualProvider");
                productLocation.setLatitude(product.getLocation().latitude);
                productLocation.setLongitude(product.getLocation().longitude);
                float dist = lastKnownLocation.distanceTo(productLocation);
                TextView distanceAway = (TextView) productBox.findViewById(R.id.distanceAway);
                distanceAway.setText(Math.round(dist) + " metres away");

                // Link the Product Box to the Product Page
                productBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), ProductPageActivity.class);

                        intent.putExtra("product", product);
                        intent.putExtra("contributor", product.getContributor());
                        intent.putExtra("profilePicId", product.getContributor().getProfileIcon());
                        intent.putIntegerArrayListExtra("productPicId", (ArrayList<Integer>) product.getImages());

                        startActivity(intent);
                    }
                });
            }
        });
        productBoxHidden = false;
    }

    // Hide the product box if not pointing at any product
    private void hideProductBox() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View productBox = findViewById(R.id.productBoxAR);
                productBox.setVisibility(View.INVISIBLE);
            }
        });
        productBoxHidden = true;
    }

    // Rotates compass to the specified angle to the north
    private void rotateCompass(double angle) {
        // Convert angle to positive degrees
        if (angle < 0) angle = angle + Math.PI;
        float angleDeg = (float) (angle * 180/Math.PI);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View compassButton = findViewById(R.id.regenerate_button);
                ObjectAnimator.ofFloat(compassButton, "rotation", (float) lastCompassButtonAngle, angleDeg).start();
                lastCompassButtonAngle = angleDeg;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent touchEvent){
        switch(touchEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                System.out.println("down: " + x1 + ", " + y1);
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                System.out.println("up: " + x2 + ", " + y2);
                if (Math.abs(x1)+ TOUCH_OFFSET < Math.abs(x2)) {
                    Intent i = new Intent(ARActivity.this, FeedActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                } else if((Math.abs(x1) > Math.abs(x2)+ TOUCH_OFFSET)) {
                    Intent i = new Intent(ARActivity.this, ProfileActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else if ((y2 < y1+ TOUCH_OFFSET) || (Math.abs(x2-x1) < TAP_OFFSET && Math.abs(y2-y1) < TAP_OFFSET)) {
                    Intent i = new Intent(ARActivity.this, MapsActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                }
                break;
        }
        return false;
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