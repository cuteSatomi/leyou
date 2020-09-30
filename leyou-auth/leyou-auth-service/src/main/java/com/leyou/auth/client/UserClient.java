package com.leyou.auth.client;

import com.leyou.user.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author zzx
 * @date 2020-09-30 13:50:17
 */
@FeignClient("user-service")
public interface UserClient extends UserApi {

}
