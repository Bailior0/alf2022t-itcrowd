
type  StatusNames = "OK" | "Error";

export class APIResponse<T> {
  public data: T | string[];
  public status: StatusNames;

  constructor(data: T, status: StatusNames) {
    this.data = data;
    this.status = status;
  }
}
