package com.starcrosser.inspiration.guide;

/**
 * 指导书里的一章。
 * <p>
 * 标题与正文都只存语言文件的键，正文本身写在语言文件里（允许用 {@code \n} 手动分段）——
 * 这样加一章、改一段文案都不用碰 Java 代码，中英文也能各自成篇。
 *
 * @param titleKey 标题的语言键
 * @param bodyKey  正文的语言键
 */
public record GuideChapter(String titleKey, String bodyKey) {
}
