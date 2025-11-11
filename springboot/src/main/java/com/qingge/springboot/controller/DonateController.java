package com.qingge.springboot.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelWriter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.net.URLEncoder;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingge.springboot.common.Result;
import org.springframework.web.multipart.MultipartFile;
import com.qingge.springboot.entity.User;
import com.qingge.springboot.utils.TokenUtils;

import com.qingge.springboot.service.IDonateService;
import com.qingge.springboot.entity.Donate;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.apache.commons.codec.digest.DigestUtils;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import java.util.stream.Collectors;

 
@RestController
@RequestMapping("/donate")
public class DonateController {

    @Resource
    private IDonateService donateService;

    private final String now = DateUtil.now();

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Donate donate) {
        donateService.saveOrUpdate(donate);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        donateService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        donateService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(donateService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(donateService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String name,
                           @RequestParam(defaultValue = "") String expressNo,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<Donate> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }
        if (!"".equals(expressNo)) {
            queryWrapper.like("express_no", expressNo);
        }
        return Result.success(donateService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
    * 导出接口
    */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        List<Donate> list = donateService.list();
        // 在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("Donate信息表", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();

        }

    /**
     * excel 导入
     * @param file
     * @throws Exception
     */
    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        // 通过 javabean的方式读取Excel内的对象，但是要求表头必须是英文，跟javabean的属性要对应起来
        List<Donate> list = reader.readAll(Donate.class);

        donateService.saveBatch(list);
        return Result.success();
    }

    private User getUser() {
        return TokenUtils.getCurrentUser();
    }

    @GetMapping("/express/{com}/{nu}")
    public Result queryExpress(@PathVariable String com, @PathVariable String nu) {
        try {
            // 使用正确的API地址
            String url = "https://poll.kuaidi100.com/poll/query.do";
            String key = "fPyfEVuh8409"; 
            String customer = "ED649E4FD300801207BA86C69676AD53";

            // 构建param参数
            Map<String, String> param = new HashMap<>();
            param.put("com", com);
            param.put("num", nu);
            param.put("resultv2", "4"); // 开启高级物流状态
            param.put("show", "0");     // JSON格式
            param.put("order", "desc"); // 降序
            param.put("lang", "zh");    // 中文

            // 将param转换为JSON字符串
            String paramJson = JSON.toJSONString(param);

            // 生成签名：param + key + customer 的顺序进行MD5加密，转大写
            String sign = DigestUtils.md5Hex(paramJson + key + customer).toUpperCase();

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // 构建请求体
            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("customer", customer);
            requestBody.add("sign", sign);
            requestBody.add("param", paramJson);

            // 配置RestTemplate
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

            // 打印请求信息（调试用）
            System.out.println("Request URL: " + url);
            System.out.println("Request Headers: " + headers);
            System.out.println("Request Body: " + requestBody);
            System.out.println("Param JSON: " + paramJson);
            System.out.println("Sign: " + sign);

            // 发送POST请求
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            // 打印响应信息（调试用）
            System.out.println("Response Status: " + response.getStatusCode());
            System.out.println("Response Headers: " + response.getHeaders());
            System.out.println("Response Body: " + response.getBody());

            // 检查响应状态
            if (response.getStatusCode().is3xxRedirection()) {
                return Result.error("500", "API重定向错误，请检查API地址配置");
            }

            String responseBody = response.getBody();
            if (responseBody == null) {
                return Result.error("500", "API响应为空");
            }

            try {
                JSONObject result = JSON.parseObject(responseBody);
                String status = result.getString("status");
                
                // 处理不同的状态码
                switch (status) {
                    case "200":
                        return Result.success(result);
                    case "400":
                        return Result.error("400", "找不到对应公司，请检查快递公司编码");
                    case "408":
                        return Result.error("408", "快递公司参数异常：验证码错误");
                    case "500":
                        return Result.error("500", "查询无结果，请稍后再试");
                    case "501":
                        return Result.error("501", "服务器错误，请稍后再试");
                    case "502":
                        return Result.error("502", "服务器繁忙，请稍后再试");
                    case "503":
                        return Result.error("503", "验证签名失败，请检查配置");
                    case "601":
                        return Result.error("601", "key已过期，请续费");
                    default:
                        return Result.error(status, result.getString("message"));
                }
            } catch (Exception e) {
                System.out.println("JSON解析错误: " + e.getMessage());
                System.out.println("响应内容: " + responseBody);
                return Result.error("500", "API响应格式错误：" + e.getMessage() + "，响应内容：" + responseBody);
            }
        } catch (Exception e) {
            System.out.println("请求异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error("500", "接口调用失败：" + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

}
