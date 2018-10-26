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
import {Router} from "@angular/router";

@Injectable()
export class Interceptor implements HttpInterceptor {
  constructor(private cookieService: CookieService,
              private router: Router) { }
  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {

    let updatedRequest = request;

    if(request.headers.get("Authorization") == null && this.cookieService.get("Authorization") != null){
      updatedRequest = request.clone({
        headers: request.headers.set("Authorization", "Bearer " + this.cookieService.get("Authorization"))
      });
    }

    return next.handle(updatedRequest).pipe(
      tap(
        event => {
        },
        error => {
          if(error['error']['error'] != null && error['error']['error'] != "invalid_grant"){
            this.cookieService.delete("Authorization");
            this.router.navigate(['/login', "expired"]);
          }
        }
      )
    );
  }
}
