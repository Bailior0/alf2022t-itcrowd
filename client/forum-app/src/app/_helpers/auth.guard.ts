import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';

import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(
    private router: Router,
    private accountService: AuthService
  ) {}

  async canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const user = this.accountService.userValue;
    console.log("can activate");
    if (user) {
      // authorised so return true
      return true;
    }
    try  {
      const a = (await this.accountService.getIsLoggedIn().toPromise());
      if(a) {
        return true;
      }
    } catch (_) {

    }
    // not logged in so redirect to login page with the return url
    this.router.navigate(['/account/login'], {queryParams: {returnUrl: state.url}});
    return false;
  }
}
