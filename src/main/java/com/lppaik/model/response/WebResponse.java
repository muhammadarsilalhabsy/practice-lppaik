package com.lppaik.model.response;

import com.lppaik.model.response.PagingResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebResponse<T> {

  private T data;

  private PagingResponse paging;

}
