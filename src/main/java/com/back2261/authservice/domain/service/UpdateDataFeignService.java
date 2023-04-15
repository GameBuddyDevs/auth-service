package com.back2261.authservice.domain.service;

import com.back2261.authservice.interfaces.response.FeignResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "predict-service", url = "${update-service.url}")
public interface UpdateDataFeignService {

    @GetMapping(value = "/updateData")
    FeignResponse updateData();
}
