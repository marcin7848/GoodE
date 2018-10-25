import { Injectable } from "@angular/core";
import { tap } from "rxjs/operators";
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpResponse,
  HttpErrorResponse
} from "@angular/common/http";
import {Observable} from "rxjs";
import { CookieService } from 'ngx-cookie-service';

@Injectable()
export class Interceptor implements HttpInterceptor {
  constructor(private cookieService: CookieService) { }
  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    const updatedRequest = request.clone({
      headers: request.headers.set("Authorization", "Bearer " + this.cookieService.get("Authorization"))
    });
    
    return next.handle(updatedRequest).pipe(
      tap(
        event => {
          //logging the http response to browser's console in case of a success
          if (event instanceof HttpResponse) {
            console.log("api call success :", event);
          }
        },
        error => {
          //logging the http response to browser's console in case of a failuer
          if (event instanceof HttpResponse) {
            console.log("api call error :", event);
          }
        }
      )
    );
  }
}
