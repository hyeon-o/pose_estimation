/* Copyright 2021 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================
*/

package org.tensorflow.lite.examples.poseestimation

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Process
import android.os.SystemClock
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.examples.poseestimation.camera.CameraSource
import org.tensorflow.lite.examples.poseestimation.exercise.RebornExercise
import org.tensorflow.lite.examples.poseestimation.http.HttpClient
import org.tensorflow.lite.examples.poseestimation.http.model.BaseResVo
import org.tensorflow.lite.examples.poseestimation.http.model.Exercise
import org.tensorflow.lite.examples.poseestimation.http.model.User
import org.tensorflow.lite.examples.poseestimation.ml.data.AnglePart
import org.tensorflow.lite.examples.poseestimation.ml.data.BodyPart
import org.tensorflow.lite.examples.poseestimation.ml.data.Device
import org.tensorflow.lite.examples.poseestimation.ml.data.Person
import org.tensorflow.lite.examples.poseestimation.ml.model.ModelType
import org.tensorflow.lite.examples.poseestimation.ml.model.MoveNet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {
    companion object {
        private const val FRAGMENT_DIALOG = "dialog"
    }

    /** A [SurfaceView] for camera preview.   */
    private lateinit var surfaceView: SurfaceView

    /** Default pose estimation model is 1 (MoveNet Thunder)
     * 0 == MoveNet Lightning model
     * 1 == MoveNet Thunder model
     * 2 == MoveNet MultiPose model
     * 3 == PoseNet model
     **/
    private var modelPos = 1

    /** Default device is CPU */
    private var device = Device.CPU

    private lateinit var tbtnExercise: ToggleButton
    private lateinit var tvExercise: TextView
    private lateinit var chrProgram: Chronometer
    private lateinit var tvCircleTime: TextView
    private lateinit var tvRestTime: TextView
    private lateinit var listJointAngle: LinearLayout
    private lateinit var jointAngleTvs: MutableMap<Int, TextView>
    private lateinit var listKeyPoint: LinearLayout
    private lateinit var keyPointTvs: MutableMap<Int, TextView>
    private lateinit var tvScore: TextView

    private lateinit var tvFPS: TextView
    private lateinit var spnDevice: Spinner
    private lateinit var spnModel: Spinner
    private lateinit var user: User

    private var cameraSource: CameraSource? = null
    private var exercise: Exercise? = null
    private var rebornExercise: RebornExercise? = null
    private var exerciseTimer: CountDownTimer? = null
    private var restTimer: CountDownTimer? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                openCamera()
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                ErrorDialog.newInstance(getString(R.string.tfe_pe_request_permission))
                    .show(supportFragmentManager, FRAGMENT_DIALOG)
            }
        }
    private var changeModelListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            // do nothing
        }

        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            changeModel(position)
        }
    }

    private var changeDeviceListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            changeDevice(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // do nothing
        }
    }

    /* ==========================
    운동 시작 버튼 리스너
    ========================== */
    private var setExerciseListener =
        CompoundButton.OnCheckedChangeListener { btn, isChecked ->
            if (isChecked) {
                Log.i("Exercise", "운동 토클 버튼 활성화")

                // 운동 데이터 조회
                val exerciseCall = HttpClient.rebornFitApi.getExercise(1)
                exerciseCall.enqueue(object : Callback<BaseResVo<Exercise>> {
                    override fun onResponse(
                        call: Call<BaseResVo<Exercise>>,
                        response: Response<BaseResVo<Exercise>>
                    ) {
                        exercise = response.body()!!.data
                        showToast("운동 데이터 조회 ${exercise!!.exerciseNo}")

                        // 운동 서비스 생성
                        rebornExercise = RebornExercise(user, exercise!!, object : RebornExercise.RebornExerciseListener {
                            override fun onExercise() {
                                showToast("운동 시작")

                                tvRestTime.visibility = View.GONE
                                tvCircleTime.visibility = View.VISIBLE

                                exerciseTimer = object : CountDownTimer((exercise!!.circleTime + 1) * 1000L, 1000) {
                                    override fun onTick(p0: Long) {
                                        val sec = p0.div(1000).toInt()
                                        tvCircleTime.text = sec.toString()
                                    }

                                    override fun onFinish() {
                                        Log.i("Exercise", "운동 타이머 종료")
                                        if (exercise!!.type == "T") {
                                            rebornExercise!!.finishCircle()
                                        }
                                    }
                                }
                                exerciseTimer!!.start()
                                Log.i("Exercise", "운동 타이머 시작")
                            }

                            override fun onRest() {
                                showToast("휴식 시작")

                                tvCircleTime.visibility = View.GONE
                                tvRestTime.visibility = View.VISIBLE

                                restTimer = object : CountDownTimer((exercise!!.repTime + 1) * 1000L, 1000) {
                                    override fun onTick(p0: Long) {
                                        val sec = p0.div(1000).toInt()
                                        tvRestTime.text = sec.toString()
                                    }

                                    override fun onFinish() {
                                        Log.i("Exercise", "휴식 타이머 종료")
                                        rebornExercise!!.startCircle()
                                    }
                                }
                                restTimer!!.start()
                                Log.i("Exercise", "휴식 타이머 시작")
                            }

                            override fun onFinish() {
                                showToast("운동 종료")

                                runOnUiThread {
                                    btn.toggle()
                                }
                            }
                        })
                        cameraSource?.setRebornExercise(rebornExercise)
                    }

                    override fun onFailure(call: Call<BaseResVo<Exercise>>, t: Throwable) {
                        Log.e(t.message, t.message, t)
                        showToast("운동 데이터 조회 실패")
                    }
                })

                // 프로그램 시작
                chrProgram.base = SystemClock.elapsedRealtime()
                chrProgram.start()

                // 컴포넌트 노출 활성화
                tvExercise.visibility = View.VISIBLE
                chrProgram.visibility = View.VISIBLE
                tvCircleTime.visibility = View.VISIBLE

            } else {
                Log.i("Exercise", "운동 토클 버튼 비활성화")

                rebornExercise = null
                exerciseTimer = null
                restTimer = null
                cameraSource?.setRebornExercise(rebornExercise)
                chrProgram.stop()

                // 컴포넌트 노출 비활성화
                tvExercise.visibility = View.GONE
                chrProgram.visibility = View.GONE
                tvCircleTime.visibility = View.GONE
                tvRestTime.visibility = View.GONE
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // keep screen on while app is running
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        tbtnExercise = findViewById(R.id.tbtnExercise)
        tvExercise = findViewById(R.id.tvExercise)
        chrProgram = findViewById(R.id.chrProgram)
        tvCircleTime = findViewById(R.id.tvCircleTime)
        tvRestTime = findViewById(R.id.tvRestTime)

        listJointAngle = findViewById(R.id.listJointAngle)
        jointAngleTvs = mutableMapOf()
        enumValues<AnglePart>().forEach {
            val tv = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                text = getString(R.string.tfe_pe_tv_disable, it.name, it.position)
            }
            jointAngleTvs[it.position] = tv
            listJointAngle.addView(tv)
        }
        listKeyPoint = findViewById(R.id.listKeyPoint)
        keyPointTvs = mutableMapOf()
        enumValues<BodyPart>().forEach {
            val tv = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                text = getString(R.string.tfe_pe_tv_disable, it.name, it.position)
            }
            keyPointTvs[it.position] = tv
            listKeyPoint.addView(tv)
        }
        tvScore = findViewById(R.id.tvScore)
        tvFPS = findViewById(R.id.tvFps)
        spnModel = findViewById(R.id.spnModel)
        spnDevice = findViewById(R.id.spnDevice)
        surfaceView = findViewById(R.id.surfaceView)
        initSpinner()
        spnModel.setSelection(modelPos)
        tbtnExercise.setOnCheckedChangeListener(setExerciseListener)
        if (!isCameraPermissionGranted()) {
            requestPermission()
        }

        // 사용자 데이터 조회
        val userCall = HttpClient.rebornFitApi.getUser(1)
        userCall.enqueue(object : Callback<BaseResVo<User>> {
            override fun onResponse(
                call: Call<BaseResVo<User>>,
                response: Response<BaseResVo<User>>
            ) {
                user = response.body()!!.data
                Log.i("사용자데이터조회", user.userNm)
                showToast("사용자 데이터 조회 ${user.userNm}")
            }

            override fun onFailure(call: Call<BaseResVo<User>>, t: Throwable) {
                Log.e("사용자데이터조회", t.message, t)
                showToast("사용자 데이터 조회 실패")
            }
        })
    }

    override fun onStart() {
        super.onStart()
        openCamera()
    }

    override fun onResume() {
        cameraSource?.resume()
        super.onResume()
    }

    override fun onPause() {
        cameraSource?.close()
        cameraSource = null
        super.onPause()
    }

    // check if permission is granted or not.
    private fun isCameraPermissionGranted(): Boolean {
        return checkPermission(
            Manifest.permission.CAMERA,
            Process.myPid(),
            Process.myUid()
        ) == PackageManager.PERMISSION_GRANTED
    }

    // open camera
    private fun openCamera() {
        if (isCameraPermissionGranted()) {
            if (cameraSource == null) {
                cameraSource =
                    CameraSource(surfaceView, object : CameraSource.CameraSourceListener {
                        override fun onFPSListener(fps: Int) {
                            tvFPS.text = getString(R.string.tfe_pe_tv_fps, fps)
                        }

                        override fun onPersonListener(person: Person) {

                            // joint angle 정보 출력
                            jointAngleTvs.forEach { (idx, tv) ->
                                val jointAngle = person.jointAngles?.get(idx)
                                jointAngle?.let {
                                    runOnUiThread {
                                        tv.text = getString(
                                            R.string.tfe_pe_tv_joint_angle,
                                            it.anglePart.name,
                                            it.anglePart.position,
                                            it.angle
                                        )
                                    }
                                }
                            }

                            // key point 정보 출력
                            keyPointTvs.forEach { (idx, tv) ->
                                val keyPoint = person.keyPoints[idx]
                                keyPoint?.let {
                                    if (it.bodyPart.isShow) {
                                        runOnUiThread {
                                            tv.text = getString(
                                                R.string.tfe_pe_tv_key_point,
                                                it.bodyPart.name,
                                                it.bodyPart.position,
                                                it.coordinate.x,
                                                it.coordinate.y,
                                                it.score
                                            )
                                        }
                                    }
                                }
                            }

                            // 운동 평가 정보 출력
                            runOnUiThread {
                                tvExercise.text = getString(
                                    R.string.tfe_pe_tv_exercise,
                                    rebornExercise?.circleCount, exercise?.circleCnt,
                                    rebornExercise?.repCount, exercise?.repCnt,
                                    rebornExercise?.totalAssess
                                )
                            }
                        }
                    }).apply {
                        prepareCamera()
                    }
                lifecycleScope.launch(Dispatchers.Main) {
                    cameraSource?.initCamera()
                }
            }
            createPoseEstimator()
        }
    }

    // Initialize spinners to let user select model/accelerator.
    private fun initSpinner() {
        // Model 선택
        ArrayAdapter.createFromResource(
            this,
            R.array.tfe_pe_models_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spnModel.adapter = adapter
            spnModel.onItemSelectedListener = changeModelListener
        }

        // Device 선택
        ArrayAdapter.createFromResource(
            this,
            R.array.tfe_pe_device_name, android.R.layout.simple_spinner_item
        ).also { adaper ->
            adaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spnDevice.adapter = adaper
            spnDevice.onItemSelectedListener = changeDeviceListener
        }
    }

    // Change model when app is running
    private fun changeModel(position: Int) {
        if (modelPos == position) return
        modelPos = position
        createPoseEstimator()
    }

    // Change device (accelerator) type when app is running
    private fun changeDevice(position: Int) {
        val targetDevice = when (position) {
            0 -> Device.CPU
            1 -> Device.GPU
            else -> Device.NNAPI
        }
        if (device == targetDevice) return
        device = targetDevice
        createPoseEstimator()
    }

    private fun createPoseEstimator() {
        val poseDetector = when (modelPos) {
            0 -> {
                // MoveNet Lightning (SinglePose)
                showDetectionScore(true)
                MoveNet.create(this, device, ModelType.Lightning)
            }
            1 -> {
                // MoveNet Thunder (SinglePose)
                showDetectionScore(true)
                MoveNet.create(this, device, ModelType.Thunder)
            }
            else -> {
                null
            }
        }
        poseDetector?.let { detector ->
            cameraSource?.setDetector(detector)
        }
    }

    // Show/hide the detection score.
    private fun showDetectionScore(isVisible: Boolean) {
        tvScore.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun requestPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) -> {
                // You can use the API that requires the permission.
                openCamera()
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Shows an error message dialog.
     */
    class ErrorDialog : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(activity)
                .setMessage(requireArguments().getString(ARG_MESSAGE))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    // do nothing
                }
                .create()

        companion object {

            @JvmStatic
            private val ARG_MESSAGE = "message"

            @JvmStatic
            fun newInstance(message: String): ErrorDialog = ErrorDialog().apply {
                arguments = Bundle().apply { putString(ARG_MESSAGE, message) }
            }
        }
    }
}
