import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor(private http: HttpClient) { }
  getAllUsers(): Observable<any> {
    return this.http.get(environment.BASE_PATH + 'users/list', httpOptions);
  }

  deleteUser(id: number): Observable<any> {
    return this.http.delete(environment.BASE_PATH + 'user/delete/'+id, httpOptions);
  }

  addUser(newuser: any): Observable<any> {
    return this.http.post(environment.BASE_PATH + 'addUser', newuser, httpOptions);
  }
  // getUserBoard(): Observable<any> {
  //   return this.http.get(environment.BASE_PATH + 'user', { responseType: 'text' });
  // }
  // getModeratorBoard(): Observable<any> {
  //   return this.http.get(environment.BASE_PATH + 'mod', { responseType: 'text' });
  // }
  // getAdminBoard(): Observable<any> {
  //   return this.http.get(environment.BASE_PATH + 'admin', { responseType: 'text' });
  // }
  
}