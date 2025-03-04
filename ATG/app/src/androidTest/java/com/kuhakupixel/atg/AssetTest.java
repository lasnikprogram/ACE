package com.kuhakupixel.atg;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.kuhakupixel.atg.backend.Asset;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AssetTest {
    @Test
    public void CopyAssetToExecutableDir() throws IOException {
        //
        String pathBin = "bin/ACE/arm64-v8a/ACE";
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        AssetManager assetManager = context.getAssets();
        InputStream expectedInputStream = assetManager.open(pathBin);
        //
        String pathToCopiedStr = Asset.CopyAssetToExecutableDir(context, pathBin);
        InputStream actualInputStream = new FileInputStream(pathToCopiedStr);
        Assert.assertTrue(IOUtils.contentEquals( expectedInputStream, actualInputStream));
    }
}