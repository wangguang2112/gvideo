package video.wang.com.rnmodule


import android.os.Bundle
import android.view.KeyEvent
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactRootView
import com.facebook.react.common.LifecycleState
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.facebook.react.shell.MainReactPackage
import com.wang.gvideo.common.base.BaseActivity
import com.wang.gvideo.common.utils.nil

/**
 * Date:2018/5/23
 * Description:
 *
 * @author wangguang.
 */
class ReactRootActivity :BaseActivity(), DefaultHardwareBackBtnHandler {
    private lateinit var mReactRootView: ReactRootView
    private var mReactInstanceManager: ReactInstanceManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mReactRootView = ReactRootView(this)
        mReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(this.application)
                .setBundleAssetName("index.android.bundle")
                .setJSMainModulePath("rn/index")
                .addPackage(MainReactPackage())
                .setUseDeveloperSupport(BuildConfig.DEBUG)
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build()
        mReactRootView.startReactApplication(mReactInstanceManager,"MyReactNativeApp",null)
        setContentView(mReactRootView)
    }

    override fun onPause() {
        super.onPause()
        mReactInstanceManager?.onHostPause(this)
    }

    override fun invokeDefaultOnBackPressed() {

    }
    override fun onResume() {
        super.onResume()
        mReactInstanceManager?.onHostResume(this,this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mReactInstanceManager?.onHostDestroy(this)
    }

    override fun onBackPressed() {
        mReactInstanceManager?.onBackPressed().nil {
            super.onBackPressed()
        }
        super.onBackPressed()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU && mReactInstanceManager != null) {
            mReactInstanceManager?.showDevOptionsDialog()
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

}