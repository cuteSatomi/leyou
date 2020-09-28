package com.leyou.goods.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * @author zzx
 * @date 2020-09-28 13:41:43
 */
@Service
public class GoodsHtmlService {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private GoodsService goodsService;

    public void createHtml(Long spuId) {
        // 初始化运行上下文
        Context context = new Context();
        // 设置数据模型
        context.setVariables(this.goodsService.loadData(spuId));

        PrintWriter printWriter = null;
        try {
            // 获取io流
            File file = new File("F:\\exe\\nginx-1.12.2\\html\\item\\" + spuId + ".html");
            printWriter = new PrintWriter(file);

            this.templateEngine.process("item", context, printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }

    public void deleteHtml(Long id) {
        File file = new File("F:\\exe\\nginx-1.12.2\\html\\item\\" + id + ".html");
        file.deleteOnExit();
    }
}
