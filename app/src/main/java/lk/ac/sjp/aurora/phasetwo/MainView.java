package lk.ac.sjp.aurora.phasetwo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lk.ac.sjp.aurora.phasetwo.FaceDetection.FaceDetectionProcessor;
import lk.ac.sjp.aurora.phasetwo.ImageClassification.ImageLabelingProcessor;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainView extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private static final String TAG = "LivePreviewActivity";
    private CameraSource cameraSource = null;
    private CameraPreview preview;
    private GraphicOverlay graphicOverlay;
    private static final int PERMISSION_REQUESTS = 1;
    private String model = "FACE_DETECTION";

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    //private View mControlsView;
    private boolean mVisible;

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            view.performClick();
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_view);

        preview = (CameraPreview) findViewById(R.id.fullscreen_content);
        if (preview == null) {
            Log.d(TAG, "Preview is null");
        }
        graphicOverlay = (GraphicOverlay) findViewById(R.id.graphicOverlay);
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null");
        }

        mVisible = true;
        //mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = preview;

        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        // Note that system bars will only be "visible" if none of the
                        // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {

                            Log.i(TAG, "THE SYSTEM BARS ARE VISIBLE");
                            hide();
                        } else {

                            hide();
                            Log.i(TAG, "THE SYSTEM BARS ARE NOT VISIBLE");
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide();
            }
        });
        if (savedInstanceState == null) {
            /* DECISION LOGIC OF AI MODEL AND ACTIVITY */
            if (allPermissionsGranted()) {
                int cameraFaceDir = CameraSource.CAMERA_FACING_FRONT;
                if (StateManager.isTeamRegistered()) {
                    if (StateManager.allScanned()) {
                        /*TODO: Add the intent to open up the final answer activity*/
                    } else {
                        model = "IMAGE_CLASSIFICATION";
                        cameraFaceDir = CameraSource.CAMERA_FACING_BACK;
                    }
                } else {
                    model = "FACE_DETECTION";
                    cameraFaceDir = CameraSource.CAMERA_FACING_FRONT;
                }
                createCameraSource(model);
                cameraSource.setFacing(cameraFaceDir);
                startCameraSource();

            } else {
                getRuntimePermissions();
            }
            /* DECISION LOGIC OF AI MODEL AND ACTIVITY */
            if (allPermissionsGranted()) {
                String model = "";
                int cameraFaceDir = CameraSource.CAMERA_FACING_FRONT;
                if (StateManager.isTeamRegistered()) {
                    if (StateManager.allScanned()) {
                        /*TODO: Add the intent to open up the final answer activity*/
                    } else {
                        model = "IMAGE_CLASSIFICATION";
                        cameraFaceDir = CameraSource.CAMERA_FACING_BACK;
                    }
                } else {
                    model = "FACE_DETECTION";
                    cameraFaceDir = CameraSource.CAMERA_FACING_FRONT;
                }
                createCameraSource(model);
                cameraSource.setFacing(cameraFaceDir);
                startCameraSource();

            } else {
                getRuntimePermissions();
            }
            /*DECISION LOGIC END*/
        } else {
            /* DECISION LOGIC OF AI MODEL AND ACTIVITY */
            if (allPermissionsGranted()) {
                String model = "";
                int cameraFaceDir = CameraSource.CAMERA_FACING_FRONT;
                if (StateManager.isTeamRegistered()) {
                    if (StateManager.allScanned()) {
                        /*TODO: Add the intent to open up the final answer activity*/
                    } else {
                        model = "IMAGE_CLASSIFICATION";
                        cameraFaceDir = CameraSource.CAMERA_FACING_BACK;
                        createCameraSource(model);
                        cameraSource.setFacing(cameraFaceDir);
                        startCameraSource();
                    }
                } else {
                    model = "FACE_DETECTION";
                    cameraFaceDir = CameraSource.CAMERA_FACING_FRONT;
                    createCameraSource(model);
                    cameraSource.setFacing(cameraFaceDir);
                    startCameraSource();
                }


            } else {
                getRuntimePermissions();
            }
            /*DECISION LOGIC END*/
        }
        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    private void createCameraSource(String model) {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
        }
        Log.i(TAG, "Using Face Detector Processor");
        if (model.equals("FACE_DETECTION")) {
            cameraSource.setMachineLearningFrameProcessor(new FaceDetectionProcessor(this));
        } else if (model.equals("IMAGE_CLASSIFICATION")) {
            cameraSource.setMachineLearningFrameProcessor(new ImageLabelingProcessor(this));
        }

    }

    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        //mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        //mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }


    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    /* PERMISSION DETAILS ARE FROM HERE ON */

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission granted: " + permission);
            return true;
        }
        Log.i(TAG, "Permission NOT granted: " + permission);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        Log.i(TAG, "Permission granted!");
        if (allPermissionsGranted()) {
            createCameraSource(model);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /*PERMISSION DETAILS END HERE*/
}

/*COMMENTED OUT CODE DUMP */
/*@SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }*/
 /*private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }*/
/*private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mContentView.setVisibility(View.VISIBLE);
            //mControlsView.setVisibility(View.VISIBLE);
        }
    };*/