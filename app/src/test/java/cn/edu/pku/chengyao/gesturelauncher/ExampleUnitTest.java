package cn.edu.pku.chengyao.gesturelauncher;

import org.junit.Test;

import cn.edu.pku.yaochg.imagesimilarity.PingYinUtil;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void pingyin_isCorrect() {
        assertEquals("llq", PingYinUtil.getFirstSpell("浏览器"));
    }
}