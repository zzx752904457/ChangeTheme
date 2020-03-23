package com.example.changetheme

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Environment
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File


class MainActivity : Activity() {

    private var themeName: String? = null

    companion object{
        const val PREF_NAME = "PREF_NAME"
        const val PREF_THEME_NAME = "PREF_THEME_NAME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventBus.getDefault().register(this)

        btn_change_1.setOnClickListener {
            initView()
            saveLocalThemeName(null)
        }
        btn_change_2.setOnClickListener {
            themeName = "theme.skin"
            ThemeSkinManager.instance.loadLocalSkin(this, "${Environment.getExternalStorageDirectory().absolutePath}${File.separator}$themeName")
            saveLocalThemeName(themeName)
        }

        btn_change_3.setOnClickListener {
            themeName = "theme-2.skin"
            ThemeSkinManager.instance.loadLocalSkin(this, "${Environment.getExternalStorageDirectory().absolutePath}${File.separator}$themeName")
            saveLocalThemeName(themeName)
        }

        initLocalTheme()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun initLocalTheme() {
        themeName = getLocalThemeName()
        if (themeName.isNullOrEmpty()) {
            initView()
        } else {
            ThemeSkinManager.instance.loadLocalSkin(this, "${Environment.getExternalStorageDirectory().absolutePath}${File.separator}$themeName")
        }
    }

    private fun getLocalThemeName(): String? {
        return getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(PREF_THEME_NAME, null)
    }

    private fun saveLocalThemeName(name: String?){
        getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putString(PREF_THEME_NAME, name).apply()
    }

    private fun initView() {
        cl_bg.setBackgroundResource(R.drawable.ic_bg)
        tv_title.setTextColor(ContextCompat.getColor(this, R.color.color_1))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateTheme(updateThemeBean: UpdateThemeBean) {
        val resources = updateThemeBean.resources
        if (resources == null) {
            initView()
        } else {
            val bgId = resources.getIdentifier("ic_bg", "drawable", updateThemeBean.pkgName)
            cl_bg.setBackgroundDrawable(resources.getDrawable(bgId))
            val colorId = resources.getIdentifier("color_1", "color", updateThemeBean.pkgName)
            tv_title.setTextColor(resources.getColor(colorId))
        }
    }
}
