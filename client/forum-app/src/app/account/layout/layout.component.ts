import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-layout',
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.scss']
})
export class LayoutComponent implements OnInit {

  constructor(
    private router: Router,
    private accountService: AuthService
  ) {
    // redirect to home if already logged in
    this.accountService.user.subscribe(
      user=>{
        console.log(user);
        if (user) {
          this.router.navigate(['/']);
        }
      }
    )
  }

  ngOnInit(): void {
  }

}
