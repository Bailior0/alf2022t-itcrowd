import {Component, Input, OnInit} from '@angular/core';
import {Post} from "../../model/Post";
import {User} from "../../model/User";
import {UserService} from "../../services/user.service";
import {map, Observable} from "rxjs";
import {PostService} from "../../services/post.service";
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-forum-post',
  templateUrl: './forum-post.component.html',
  styleUrls: ['./forum-post.component.scss']
})
export class ForumPostComponent implements OnInit {

  constructor(private usrService:UserService,private postService: PostService, private authService:AuthService, private router:Router) { }

  @Input()
  forumPost: Post | undefined;

  user: User | undefined;
  showDelete: boolean = false;
  showEdit: boolean = false;
  editContent: boolean = false;
  showCommentsText:string="showComments";
  hideCommentsText:string="hideComments";
  newcomment:string = "";
  showComments:boolean = false;
  showCommentField:boolean = false;
  comments:Post[] =[];
  newContent: string = "";
  followed:boolean = false;
  /*
  get user():User | undefined{
    if(this.forumPost && this.forumPost.author)
      return

    return undefined;
  }
   */
  saveEdit():void {
    if(this.forumPost?.id) {
      this.postService.editPost(this.forumPost?.id, this.newContent);
      this.editContent = false;
      window.location.reload();
    }
  }
  cancelEdit():void {
    this.editContent = false;
    this.newContent="";
  }
  giveComment():void{
    this.showCommentField = true;
  }

  toggleComments():void{
    this.showComments = !this.showComments;
  }
  vote(num:number):void {
    // @ts-ignore
    this.postService.postVote(this.forumPost?.id,num).subscribe((res)=> {

      // @ts-ignore
      this.forumPost?.voteCount = this.forumPost?.voteCount +num;
    });
  }
  postComment():void {
    if(this.forumPost?.id) {
      this.postService.postComment(this.forumPost?.id,this.newcomment);
      this.newcomment = "";
      this.showCommentField = false;
      window.location.reload();

    }
  }

  edit():void {
    this.newContent = this.forumPost?.content ?? "";
    this.editContent = true;
  }
  delete():void {
    if(this.forumPost?.id) {
      this.postService.deletePost(this.forumPost?.id).subscribe(res => window.location.reload());
    }
  }
  ngOnInit(): void {
    // @ts-ignore
    this.usrService.getUserDetails(this.forumPost.author).pipe(map(res => <User>res.data)).subscribe(res => {
      this.user = res;
    });
    if (this.forumPost?.id) {
      const mainUser = this.authService.userValue;

      this.postService.getComments(this.forumPost.id).subscribe(posts => {
        if (Array.isArray(posts))
          this.comments = <Post[]>posts
        if (this.forumPost?.id && mainUser != null) {
          if ((this.getIsAdmin(mainUser) || this.forumPost.author === mainUser?.id ) && this.comments.length == 0) {
            this.showDelete = true;
          }
          if (this.forumPost.author === mainUser.id) {
            this.showEdit = true;
          }
        }
      });
      this.getFollow();
    }
  }

  getIsAdmin(user:User):boolean {
    return ( user.roles.find(element => element ==="ADMIN")!= undefined) ;
  }
  getFollow():void {
    if(this.forumPost?.author) {
      this.usrService.getIsFollow(this.forumPost?.author).subscribe(result => {
           // @ts-ignore
        this.followed = result;
      });

    }
  }
  follow():boolean{
      // @ts-ignore
    this.usrService.follow(this.forumPost.author).subscribe(result => {
      if(result)
        this.followed = true;
      window.location.reload();


    });
    return false;
  }
  unFollow():boolean {
      // @ts-ignore
    this.usrService.unfollow(this.forumPost.author).subscribe(result => {
      if(result)
        this.followed = false;
      window.location.reload();
    });
    return false;
  }
}


