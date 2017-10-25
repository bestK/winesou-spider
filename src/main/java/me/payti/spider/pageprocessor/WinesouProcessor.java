package me.payti.spider.pageprocessor;

import com.alibaba.fastjson.JSONObject;
import me.payti.spider.model.WImage;
import me.payti.spider.model.Wine;
import me.payti.spider.util.CreateFileUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by KAI on 2017/10/24.
 * ectest@foxmail.com
 */
public class WinesouProcessor implements PageProcessor {

    private static final String WINE_URL = "https://www.winesou.com/index.php?caid=1&searchword=%E8%B5%B7%E6%B3%A1";

    private static final String SAVE_PATH = "C:\\Users\\LV\\Desktop\\wines\\";

    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

    private static List<Wine> wines = new ArrayList<>();


    @Override
    public void process(Page page) {

        page.addTargetRequests(page.getHtml().xpath("//a[@class='porpic l']/@href").all());


        // 图片地址
        String all = page.getHtml().xpath("//div[@class='big']/ul/li[1]/img/@src").get();
        String body = page.getHtml().xpath("//div[@class='big']/ul/li[2]/img/@src").get();
        String logo = page.getHtml().xpath("//div[@class='big']/ul/li[3]/img/@src").get();
        // 中文名称
        String cnName = page.getHtml().xpath("//div[@class='WS_jiukuanT']/h1[@class='clearfix']/em/text()").get();
        // 英文名称
        String enName = page.getHtml().xpath("//div[@class='WS_jkHead']/h2/text()").get();
        // 出产国家
        String country = page.getHtml().xpath("//table/tbody/tr[1]/td[2]/a/@title").get();
        // 出产地区
        String area = page.getHtml().xpath("//table/tbody/tr[2]/td[2]/a/@title").get();
        // 红酒品牌
        String brand = page.getHtml().xpath("//table/tbody/tr[3]/td[2]/a/@title").get();
        // 类型
        String model = page.getHtml().xpath("//table/tbody/tr[4]/td[2]/text()").get();
        // 葡萄品种
        String breed = page.getHtml().xpath("//table/tbody/tr[5]/td[2]/a/text()").get();
        // 净含量
        String ml = page.getHtml().xpath("//table/tbody/tr[6]/td[2]/text()").get();
        // 酒精度
        String vol = page.getHtml().xpath("//table/tbody/tr[7]/td[2]/text()").get();

        // 用途
        String use = page.getHtml().xpath("//table/tbody/tr[9]/td[2]/text()").get();
        // 年份
        String year = page.getHtml().xpath("//div[@id='nianfen']/div[@class='WS_jiukuangdes']/table/tbody/tr[2]/td[1]/text()").get();
        // 价格区间
        String price = page.getHtml().xpath("//div[@id='nianfen']/div[@class='WS_jiukuangdes']/table/tbody/tr[2]/td[5]/text()").get();

        Wine wine = new Wine();

        WImage image = new WImage();
        image.setAll(all);
        image.setBody(body);
        image.setLogo(logo);

        wine.setwImage(image);
        wine.setCnName(cnName);
        wine.setEnName(enName);
        wine.setCountry(country);
        wine.setArea(area);
        wine.setBrand(brand);
        wine.setModel(model);
        wine.setBreed(breed);
        wine.setMl(ml);
        wine.setVol(vol);
        wine.setUse(use);
        wine.setYear(year);
        wine.setPrice(price);

        wines.add(wine);
    }

    @Override
    public Site getSite() {
        return site;
    }

    // 启动爬虫
    public static void main(String[] args) throws Exception {
        Spider.create(new WinesouProcessor()).addUrl(WINE_URL).thread(5).run();
        wines.remove(0);
        if (!wines.isEmpty()) {
            for (Wine wine : wines) {
                String all;
                String body;
                String logo;
                if (wine != null && !"null".equals(wine.getCountry())) {
                    all = wine.getwImage().getAll();
                    body = wine.getwImage().getBody();
                    logo = wine.getwImage().getLogo();
                    CreateFileUtil.createJsonFile(JSONObject.toJSONString(wine), SAVE_PATH + wine.getCnName().trim(), wine.getCnName());
                    CreateFileUtil.download(all, SAVE_PATH + wine.getCnName().trim(), all.substring(all.lastIndexOf("/") + 1, all.length()));
                    CreateFileUtil.download(body, SAVE_PATH + wine.getCnName().trim(), body.substring(body.lastIndexOf("/") + 1, all.length()));
                    CreateFileUtil.download(logo, SAVE_PATH + wine.getCnName().trim(), logo.substring(logo.lastIndexOf("/") + 1, all.length()));
                }
            }

            System.out.println("total:" + wines.size());
        }

    }


    public static <T> void removeNullElements(List<T> list) {
        list.removeAll(Collections.singleton(null));
    }
}
