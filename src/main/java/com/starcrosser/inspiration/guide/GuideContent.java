package com.starcrosser.inspiration.guide;

import java.util.List;

/**
 * 指导书的全部内容。
 * <p>
 * 目前只做「基础介绍」：先交代灵感是什么，再把六台机器与注入结构一次讲清楚。
 * 章节目录是固定顺序的 {@link List}，翻页就是在这张表上前后走 —— 以后要接着写，
 * 往 {@link #CHAPTERS} 里加一项、再去语言文件补两条文案即可。
 */
public final class GuideContent {

    /** 章节目录（服务端与客户端共用，不需要同步）。 */
    public static final List<GuideChapter> CHAPTERS = List.of(
            new GuideChapter("guide.inspiration.chapter.inspiration",
                    "guide.inspiration.chapter.inspiration.body"),
            new GuideChapter("guide.inspiration.chapter.machines",
                    "guide.inspiration.chapter.machines.body"),
            new GuideChapter("guide.inspiration.chapter.energy",
                    "guide.inspiration.chapter.energy.body"),
            new GuideChapter("guide.inspiration.chapter.plugins",
                    "guide.inspiration.chapter.plugins.body"),
            new GuideChapter("guide.inspiration.chapter.injection",
                    "guide.inspiration.chapter.injection.body"),
            new GuideChapter("guide.inspiration.chapter.radiation",
                    "guide.inspiration.chapter.radiation.body"));

    private GuideContent() {
    }

    /** 章节总数。 */
    public static int size() {
        return CHAPTERS.size();
    }

    /** 取一章，越界时夹回范围内 —— 界面上的翻页按钮就不必各自判边界了。 */
    public static GuideChapter chapter(int index) {
        return CHAPTERS.get(Math.max(0, Math.min(CHAPTERS.size() - 1, index)));
    }
}
