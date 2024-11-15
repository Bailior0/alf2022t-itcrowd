import { Component, OnInit } from '@angular/core';
import {PostService} from "../services/post.service";
import {Post} from "../model/Post";
import {first} from "rxjs";
import {AuthService} from "../services/auth.service";

@Component({
  selector: 'app-main-view',
  templateUrl: './main-view.component.html',
  styleUrls: ['./main-view.component.scss']
})
export class MainViewComponent implements OnInit {

  constructor(private postService: PostService, public authService: AuthService) { }

  posts : Post[] = [];
  followedPosts : Post[] = [];

  ngOnInit(): void {
    this.postService.getTopPosts()
      .pipe(first())
      .subscribe(posts =>{
        if(Array.isArray(posts))
          this.posts = <Post[]>posts
      });
    this.postService.getFollowedTopPosts()
      .pipe(first())
      .subscribe(posts =>{
        if(Array.isArray(posts))
          this.followedPosts = <Post[]>posts
      });
  }

}
