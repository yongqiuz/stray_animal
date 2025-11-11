package com.qingge.springboot.service.impl;

import com.qingge.springboot.entity.Applcation;
import com.qingge.springboot.mapper.ApplcationMapper;
import com.qingge.springboot.service.IApplcationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

 
@Service
public class ApplcationServiceImpl extends ServiceImpl<ApplcationMapper, Applcation> implements IApplcationService {

}
