import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ForumPostComponent } from './components/forum-post/forum-post.component';
import { MainViewComponent } from './main_view/main-view.component';
import {MatFormFieldModule} from "@angular/material/form-field";
import {FormsModule} from "@angular/forms";
import {MatInputModule} from "@angular/material/input";
import { CreatePostComponent } from './components/create-post/create-post.component';
import { MatButtonModule} from "@angular/material/button";
import {CommonModule} from "@angular/common";
import {AuthService} from "./services/auth.service";
import {HTTP_INTERCEPTORS, HttpClient, HttpClientModule} from "@angular/common/http";
import {RequestAuthInterceptor} from "./_helpers/request.authenticator";

@NgModule({
  declarations: [
    AppComponent,
    ForumPostComponent,
    MainViewComponent,
    CreatePostComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatFormFieldModule,
    FormsModule,
    MatInputModule,
    MatButtonModule,
    CommonModule,
    HttpClientModule
  ],
  providers: [
    AuthService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: RequestAuthInterceptor ,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {

}
