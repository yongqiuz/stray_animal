package com.qingge.springboot.service.impl;

import com.qingge.springboot.entity.Activity;
import com.qingge.springboot.mapper.ActivityMapper;
import com.qingge.springboot.service.IActivityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

 
@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements IActivityService {

}
