import { Injectable } from '@angular/core';
import {User} from "../model/User";
import {HttpClient} from "@angular/common/http";
import {APIStrings} from "./APIStrings";
import {firstValueFrom, map, of} from "rxjs";
import {APIResponse} from "../model/Response";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) { }

  public getUserDetails(id:string){

    return this.http.get<APIResponse<User>>(`${APIStrings.USER}/${id}`);

  }
  getIsFollow(id:string) {
    return this.http.get<APIResponse<boolean>>(`${APIStrings.FOLLOW}/${id}`).pipe(map(res => res.data));
  }

  follow(id:string){
    return this.http.post<APIResponse<boolean>>(`${APIStrings.FOLLOW}/${id}`,{}).pipe(map(res => res.status ==="OK"));
  }
  unfollow(id:string){
    return this.http.delete<APIResponse<boolean>>(`${APIStrings.FOLLOW}/${id}`).pipe(map(res => res.status ==="OK"));
  }
}
