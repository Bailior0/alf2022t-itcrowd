import { Component, OnInit } from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  constructor(private authService: AuthService,private route: ActivatedRoute,private router: Router) { }
  username:string ="";
  password:string ="";
  email: string = "";
  ngOnInit(): void {
  }
  register(): void {
    this.authService?.register(this.username,this.email,this.password).toPromise().then((r) => {
      this.router.navigate(['/account/login']);
    });
  }
}
