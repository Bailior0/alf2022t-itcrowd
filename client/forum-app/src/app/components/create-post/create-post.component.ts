import { Component, OnInit } from '@angular/core';
import {PostService} from "../../services/post.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-create-post',
  templateUrl: './create-post.component.html',
  styleUrls: ['./create-post.component.scss']
})
export class CreatePostComponent implements OnInit {

  constructor(private postService: PostService, private router:Router) { }
  title:string = "";
  body:string = "";

  post() {
    console.log("post");
    this.postService.postPost(this.title, this.body).subscribe(r => {
        window.location.reload();
      }
    );
  }
  ngOnInit(): void {

  }

}
