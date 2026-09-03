/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import { ApiResponseUserProfileResponse } from './get-user-profile';

export interface UpdateUserProfileRequest {
  firstName: string;
  lastName: string;
  birthDate?: string;
}

export interface UpdateUserProfile$Params {
  body: UpdateUserProfileRequest;
}

export function updateUserProfile(http: HttpClient, rootUrl: string, params: UpdateUserProfile$Params, context?: HttpContext): Observable<StrictHttpResponse<ApiResponseUserProfileResponse>> {
  const rb = new RequestBuilder(rootUrl, updateUserProfile.PATH, 'patch');
  if (params) {
    rb.body(params.body, 'application/json');
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<ApiResponseUserProfileResponse>;
    })
  );
}

updateUserProfile.PATH = '/users/me';
