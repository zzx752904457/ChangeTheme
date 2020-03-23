package com.example.changetheme

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.util.Log
import org.greenrobot.eventbus.EventBus


class ThemeSkinManager {
    var resources: Resources? = null

    private object SingletonHolder {
        val holder = ThemeSkinManager()
    }

    companion object {
        val instance = SingletonHolder.holder
    }

    fun loadLocalSkin(context: Context, path: String?){
        if (path.isNullOrEmpty()) {
             return
        }
        try {
            val time = System.currentTimeMillis()
            val mPm = context.packageManager
            val mInfo = mPm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES)
            val pkgName = mInfo?.packageName
            val assetManager: AssetManager = AssetManager::class.java.newInstance()
            val addAssetPath = assetManager.javaClass.getMethod("addAssetPath", String::class.java)
            addAssetPath.invoke(assetManager, path)
            resources = Resources(assetManager, context.resources.displayMetrics, context.resources.configuration)
            EventBus.getDefault().post(UpdateThemeBean(resources,pkgName))
            Log.e("读取主题用时：", "${System.currentTimeMillis() - time}")
        } catch (e : Exception) {
            e.printStackTrace();
        }
    }
}