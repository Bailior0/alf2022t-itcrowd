import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {APIStrings} from "./APIStrings";
import {Post} from "../model/Post";
import { map } from 'rxjs';
import {APIResponse} from "../model/Response";
import {User} from "../model/User";

@Injectable({
  providedIn: 'root'
})
export class PostService {

  constructor(private http: HttpClient) { }

  public getTopPosts(){
    return this.http.get<APIResponse<Post[]>>(APIStrings.POSTS +"/top").pipe(map(res => res.data))
  }

  public getFollowedTopPosts(){
    return this.http.get<APIResponse<Post[]>>(APIStrings.POSTS +"/feed").pipe(map(res => res.data))
  }

  public postPost(title:string,content:string){

    let res = this.http.post<APIResponse<Post>>(APIStrings.POSTS +"/", {title,content},);
    return res.pipe(map(res=>{
      return res;
    }));
  }

  public deletePost(id:string){
   let res = this.http.delete<APIResponse<any>>(APIStrings.POSTS +"/"+id);
  return  res.pipe(map(res=>{
      return res;
    }));
  }

  public editPost(id:string,content:string){
    let res = this.http.put(APIStrings.POSTS +"/"+id,{content});
    res.subscribe(res=>{

    })
    return res;
  }

  public  getComments(id:string) {
    return this.http.get<APIResponse<Post[]>>(APIStrings.POSTS+"/"+id+"/comments",{}).pipe(map(res => res.data));
  }

  public  postComment(id:string,content:string) {
    let res = this.http.post<APIResponse<Post>>(APIStrings.POSTS+"/"+id+"/comments",{content});

    res.subscribe(res=>{
      if(res.status=="OK" ) {

      }
    })
    return res;
  }
  postVote(id:string, amount:number) {
   return this.http.post<APIResponse<boolean>>( APIStrings.VOTE+"/"+id,{amount:amount}).pipe(map(res => res.data));
  }
}
