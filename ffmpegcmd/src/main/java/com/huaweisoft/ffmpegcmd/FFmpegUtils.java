package com.huaweisoft.ffmpegcmd;

/**
 * FFmpeg功能模块（只有命令行功能）
 * Created by ousy on 2016/10/11.
 */

public class FFmpegUtils
{
    private static FFmpegUtils sInstance = null;

    /**
     * 全局单一实例
     */
    public synchronized static FFmpegUtils getInstance()
    {
        if (null == sInstance)
        {
            sInstance = new FFmpegUtils();
        }

        return sInstance;
    }

    /**
     * FFmpeg执行命令行的方法
     * @param argc 指令以空格分割的字段个数
     * @param argv 指令
     * @return 0：为成功
     */
    public int ffmpegCmd(int argc, String[] argv)
    {
        int ret = ffmpegcore(argc, argv);

        return ret;
    }

    public native int ffmpegcore(int argc, String[] argv);

    static
    {
        System.loadLibrary("avutil-54");
        System.loadLibrary("swresample-1");
        System.loadLibrary("avcodec-56");
        System.loadLibrary("avformat-56");
        System.loadLibrary("swscale-3");
        System.loadLibrary("postproc-53");
        System.loadLibrary("avfilter-5");
        System.loadLibrary("avdevice-56");
        System.loadLibrary("sfftranscoder");
    }
}
