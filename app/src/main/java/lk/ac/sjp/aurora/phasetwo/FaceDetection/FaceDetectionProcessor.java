// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package lk.ac.sjp.aurora.phasetwo.FaceDetection;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.io.IOException;
import java.util.List;

import lk.ac.sjp.aurora.phasetwo.Dialogs.TeamRecognizedDialog;
import lk.ac.sjp.aurora.phasetwo.FrameMetadata;
import lk.ac.sjp.aurora.phasetwo.GraphicOverlay;
import lk.ac.sjp.aurora.phasetwo.StateManager;
import lk.ac.sjp.aurora.phasetwo.VisionProcessorBase;

/**
 * Face Detector Demo.
 */
public class FaceDetectionProcessor extends VisionProcessorBase<List<FirebaseVisionFace>> {

    private static final String TAG = "FaceDetectionProcessor";
    private Activity activity;
    private final FirebaseVisionFaceDetector detector;

    public FaceDetectionProcessor(Activity activity) {
        this.activity = activity;
        Log.i(TAG, "FACE DETECTION PROCESSOR CONSTRUCTED");
        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setClassificationType(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .setLandmarkType(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .setTrackingEnabled(true)
                        .build();

        detector = FirebaseVision.getInstance().getVisionFaceDetector(options);
    }

    @Override
    public void stop() {
        try {
            detector.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while trying to close Face Detector: " + e);
        }
    }

    @Override
    protected Task<List<FirebaseVisionFace>> detectInImage(FirebaseVisionImage image) {
        return detector.detectInImage(image);
    }

    @Override
    protected void onSuccess(@NonNull List<FirebaseVisionFace> faces, @NonNull FrameMetadata frameMetadata, @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        for (int i = 0; i < faces.size(); ++i) {
            FirebaseVisionFace face = faces.get(i);
            FaceGraphic faceGraphic = new FaceGraphic(graphicOverlay);
            graphicOverlay.add(faceGraphic);
            faceGraphic.updateFace(face, frameMetadata.getCameraFacing());
        }
        if (faces.size() != 0) {
            FirebaseVisionFace firebaseVisionFace = faces.get(0);
            if (!StateManager.isTeamRegistered()) {
                TeamRecognizedDialog teamRecognizedDialog = new TeamRecognizedDialog();
                teamRecognizedDialog.setMessage("Are you from Team A?");
                teamRecognizedDialog.setActivity(activity);
                teamRecognizedDialog.show(activity.getFragmentManager(), "Team Recognizer");
            }
        }

    }

/*  @Override
  protected void onSuccess(
      @NonNull List<FirebaseVisionFace> faces,
      @NonNull FrameMetadata frameMetadata,
      @NonNull GraphicOverlay graphicOverlay) {

  }*/

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "Face detection failed " + e);
    }
}
