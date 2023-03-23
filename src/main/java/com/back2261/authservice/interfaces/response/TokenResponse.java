package com.back2261.authservice.interfaces.response;

import com.back2261.authservice.interfaces.dto.TokenResponseBody;
import io.github.GameBuddyDevs.backendlibrary.base.BaseResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponse extends BaseResponse<TokenResponseBody> {}
