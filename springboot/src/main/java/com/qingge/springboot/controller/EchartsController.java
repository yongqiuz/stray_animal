package com.qingge.springboot.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Quarter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingge.springboot.common.Result;
import com.qingge.springboot.entity.Animal;
import com.qingge.springboot.entity.User;
import com.qingge.springboot.service.IAnimalService;
import com.qingge.springboot.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("/echarts")
public class EchartsController {
    private static final Logger logger = LoggerFactory.getLogger(EchartsController.class);

    @Autowired
    private IUserService userService;

    @Resource
    private IAnimalService animalService;

    @GetMapping("/example")
    public Result get() {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("x", CollUtil.newArrayList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"));
            map.put("y", CollUtil.newArrayList(150, 230, 224, 218, 135, 147, 260));
            return Result.success(map);
        } catch (Exception e) {
            logger.error("获取示例数据失败", e);
            return Result.error("500", "获取示例数据失败");
        }
    }

    @GetMapping("/members")
    public Result members() {
        try {
            List<User> list = userService.list();
            int q1 = 0; // 第一季度
            int q2 = 0; // 第二季度
            int q3 = 0; // 第三季度
            int q4 = 0; // 第四季度
            for (User user : list) {
                Date createTime = user.getCreateTime();
                if (createTime == null) {
                    continue;
                }
                Quarter quarter = DateUtil.quarterEnum(createTime);
                switch (quarter) {
                    case Q1: q1 += 1; break;
                    case Q2: q2 += 1; break;
                    case Q3: q3 += 1; break;
                    case Q4: q4 += 1; break;
                    default: break;
                }
            }
            return Result.success(CollUtil.newArrayList(q1, q2, q3, q4));
        } catch (Exception e) {
            logger.error("获取会员统计数据失败", e);
            return Result.error("500", "获取会员统计数据失败");
        }
    }

    @GetMapping("/animal")
    public Result getAnimalStats() {
        try {
            logger.info("开始获取动物统计数据");
            
            // 获取所有动物数据
            List<Animal> animals = animalService.list();
            if (animals == null || animals.isEmpty()) {
                logger.warn("没有找到任何动物数据");
                return Result.success(new HashMap<String, Object>() {{
                    put("sterilizations", Arrays.asList(0, 0, 0, 0));
                    put("adoptions", Arrays.asList(0, 0, 0, 0));
                }});
            }
            
            logger.info("获取到动物数据数量: {}", animals.size());
            
            // 初始化四个季度的统计数据
            int[] sterilizations = new int[4];  // 绝育数量
            int[] adoptions = new int[4];       // 领养数量
            
            // 遍历所有动物数据
            for (Animal animal : animals) {
                Date createTime = animal.getCreateTime();
                if (createTime == null) {
                    logger.warn("动物ID: {} 没有创建时间", animal.getId());
                    continue; // 跳过没有创建时间的记录
                }
                
                Quarter quarter = DateUtil.quarterEnum(createTime);
                int quarterIndex;
                switch (quarter) {
                    case Q1: quarterIndex = 0; break;
                    case Q2: quarterIndex = 1; break;
                    case Q3: quarterIndex = 2; break;
                    case Q4: quarterIndex = 3; break;
                    default: continue;
                }
                
                // 统计绝育数量
                if ("是".equals(animal.getSterilization())) {
                    sterilizations[quarterIndex]++;
                    logger.debug("动物ID: {} 已绝育，季度: {}", animal.getId(), quarter);
                }
                
                // 统计领养数量 - 使用isAdopt字段
                if ("是".equals(animal.getIsAdopt())) {
                    adoptions[quarterIndex]++;
                    logger.debug("动物ID: {} 已领养，季度: {}", animal.getId(), quarter);
                }
            }
            
            // 构建返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("sterilizations", Arrays.asList(sterilizations));
            result.put("adoptions", Arrays.asList(adoptions));
            
            logger.info("统计数据: 绝育={}, 领养={}", 
                Arrays.toString(sterilizations), 
                Arrays.toString(adoptions));
            
            return Result.success(result);
        } catch (Exception e) {
            logger.error("获取动物统计数据失败", e);
            return Result.error("500", "获取动物统计数据失败: " + e.getMessage());
        }
    }
}
