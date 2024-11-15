
export class APIStrings {

  public static endpoint:string="http://localhost:8080";
  public static API:string="/api";
  public static AUTH:string=this.endpoint + this.API + "/auth";
  public static USER:string=this.endpoint + this.API + "/users";
  public static POSTS:string=this.endpoint + this.API + "/posts";
  public static FOLLOW:string=this.endpoint+this.API+ "/follow";
  public static VOTE:string = this.endpoint+this.API+"/vote";
}
