import { Component, OnInit } from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  constructor(private authService: AuthService,private route: ActivatedRoute,private router: Router) { }
  username:string ="";
  password:string ="";

  login(){
    console.log("login");
    this.authService.login(this.username, this.password)
      .subscribe({
      next: () => {
        // get return url from query parameters or default to home page
        const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
        this.router.navigateByUrl(returnUrl);
      },
      error: error => {
        //this.alertService.error(error);
        //this.loading = false;
      }
    });
  }

  register(){
    this.router?.navigateByUrl("/account/register");
  }

  ngOnInit(): void {
  }

}
