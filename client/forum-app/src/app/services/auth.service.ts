import { Injectable } from '@angular/core';
import {APIResponse} from "../model/Response";
import {User} from "../model/User";
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, map, Observable, of} from "rxjs";
import {APIStrings} from "./APIStrings";
import {Post} from "../model/Post";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private userSubject: BehaviorSubject<User | null>;
  public user: Observable<User | null>;

  constructor(private http: HttpClient) {
    this.userSubject = new BehaviorSubject<User|null>(null);
    this.user = this.userSubject.asObservable();
  }

  public get userValue(): User | null {
    return this.userSubject.value;
  }
  getIsLoggedIn():Observable<User> {
    const res = this.http.get<APIResponse<User>>( APIStrings.AUTH+"/");
    return res.pipe(map(res=>{
      if(res.status=="OK" ) {
        this.userSubject.next(<User>res.data);
      }
      return res.data as User;
    }));
  }

  getIsUser():boolean {
    return this.user != null;
  }

  register(username: string, email: string, password: string): Observable<APIResponse<User>> {
    let res = this.http.post<APIResponse<User>>(APIStrings.AUTH +"/signup", {username,email,password});
    return res;
  }

  login(username:string, password: string): Observable<APIResponse<User>> {

    let res = this.http.post<APIResponse<User>>(APIStrings.AUTH + "/signin",{username, password})
    res.pipe(map(res=>{
        if(res.status=="OK" ) {
          this.userSubject.next(<User>res.data);
        }
    }));
    return res;
  }

  resetPassword() {}

  async sendRequest(url: string, methodArg: string, bodyArg: any): Promise<APIResponse<any>|null> {
    let response = await fetch(url, {
      method: methodArg,
      body: (bodyArg),
      headers: {},
    });

    if(response == null)
      return null;

    return await response.json();
  }



}
