/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

export interface UserProfileResponse {
  id: number;
  firstName: string;
  lastName: string;
  fullName: string;
  email: string;
  birthDate?: string;
  roles: Array<string>;
  token?: string;
}

export interface ApiResponseUserProfileResponse {
  data?: UserProfileResponse;
  message?: string;
}

export function getUserProfile(http: HttpClient, rootUrl: string, context?: HttpContext): Observable<StrictHttpResponse<ApiResponseUserProfileResponse>> {
  const rb = new RequestBuilder(rootUrl, getUserProfile.PATH, 'get');

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<ApiResponseUserProfileResponse>;
    })
  );
}

getUserProfile.PATH = '/users/me';
