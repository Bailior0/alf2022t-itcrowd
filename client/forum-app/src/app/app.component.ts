import {Component, OnInit} from '@angular/core';

import {AuthService} from "./services/auth.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements  OnInit{

  isUser:boolean = false;
  title = 'forum-app';

  userService:AuthService| undefined;
  constructor(userService:AuthService) {
    this.userService = userService;
  }

  ngOnInit(): void {
    this.isUser = this.userService?.getIsUser() ?? false;
  }
}
