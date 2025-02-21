import { Component, OnInit } from '@angular/core';
import { AuthService } from '../_services/auth.service';
import { TokenStorageService } from '../_services/token-storage.service';
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  form: any = {
    username: null,
    password: null
  };
  isLoggedIn = false;
  isLoginFailed = false;
  errorMessage = '';
  roles: string[] = [];
  constructor(private authService: AuthService, private tokenStorage: TokenStorageService) {
   }
  ngOnInit(): void {
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
      this.roles = this.tokenStorage.getUser().roles;
    }
  }

  onSubmit(): void {
    const { username, password } = this.form;
    this.authService.login(username, password).subscribe({
      next: data => {

        //let pom = JSON.parse(atob(data.accessToken.split('.')[1]));

        this.tokenStorage.saveToken(data.accessToken);
        this.tokenStorage.saveUser(data.username);
        this.tokenStorage.saveUserRole(data.role);
        this.isLoginFailed = false;
        this.isLoggedIn = true;
        this.roles = this.tokenStorage.getUser().roles;
        this.reloadPage();
    },
    error: err => {
      alert("error");
      this.errorMessage = err.error?.["message"];
      this.isLoginFailed = true;
    }
  });
  }
  reloadPage(): void {
    window.location.reload();
  }
}