package com.example.ar_reshare;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


public class addPhotoDialog extends DialogFragment {



    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        void onDialogActionClick(DialogFragment dialog, String action);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (NoticeDialogListener) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add product images").setItems(R.array.add_image_popup, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    //when user chooses camera
                    listener.onDialogActionClick(addPhotoDialog.this,"camera");
                }else if(which == 1){
                    //when user chooses gallery
                    listener.onDialogActionClick(addPhotoDialog.this,"gallery");
                }
            }
        });

        return builder.create();
    }

}


