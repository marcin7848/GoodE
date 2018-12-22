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

    if(!updatedRequest.headers.get("Content-Type")){
      updatedRequest = updatedRequest.clone({
        headers: updatedRequest.headers.set('Content-Type', 'application/json')
      });
    }

    if(!updatedRequest.headers.get("Authorization") && this.cookieService.get("Authorization")){
      updatedRequest = updatedRequest.clone({
        headers: updatedRequest.headers.set("Authorization", "Bearer " + this.cookieService.get("Authorization"))
      });
    }

    if(this.cookieService.get("Language") && (this.cookieService.get("Language") == "pl" || this.cookieService.get("Language") == "en")
    && !updatedRequest.url.match(/\/oauth\/token/g)) {
      updatedRequest = updatedRequest.clone({
        url: updatedRequest.url + '?lang=' + this.cookieService.get("Language")
      });
    }

    return next.handle(updatedRequest).pipe(
      tap(
        event => {
        },
        error => {
          if(error['status'] == 401 && error['error']['error'] != "invalid_grant"){
            this.cookieService.delete("Authorization");
            setTimeout(function(){
              this.router.navigate(['/login', "expired"]);
            }, 500);
          }
        }
      )
    );
  }
}
