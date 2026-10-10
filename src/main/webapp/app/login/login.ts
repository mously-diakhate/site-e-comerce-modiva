import { HttpErrorResponse } from '@angular/common/http';
import { AfterViewInit, Component, ElementRef, OnInit, inject, signal, viewChild } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { TranslateModule, TranslateService } from '@ngx-translate/core';

import { AccountService } from 'app/core/auth/account.service';
import { LoginService } from 'app/login/login.service';
import { RegisterService } from 'app/account/register/register.service';
import { EMAIL_ALREADY_USED_TYPE, LOGIN_ALREADY_USED_TYPE } from 'app/shared/jhipster/error.constants';
import { Authority } from 'app/shared/jhipster/constants';
import { TranslateDirective } from 'app/shared/language';

type AuthTab = 'login' | 'register';

@Component({
  selector: 'jhi-login',
  imports: [TranslateDirective, TranslateModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export default class Login implements OnInit, AfterViewInit {
  usernameInput = viewChild<ElementRef>('username');
  registerFullNameInput = viewChild<ElementRef>('registerFullName');

  readonly activeTab = signal<AuthTab>('login');
  readonly authenticationError = signal(false);
  /** true si le serveur a bloqué la connexion (trop d'échecs, réponse 429). */
  readonly tooManyAttempts = signal(false);

  readonly doNotMatch = signal(false);
  readonly registerError = signal(false);
  readonly registerErrorEmailExists = signal(false);
  readonly registerSuccess = signal(false);

  loginForm = new FormGroup({
    username: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    password: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    rememberMe: new FormControl(false, { nonNullable: true, validators: [Validators.required] }),
  });

  registerForm = new FormGroup({
    fullName: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(1), Validators.maxLength(50)],
    }),
    email: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(5), Validators.maxLength(254), Validators.email],
    }),
    password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(4), Validators.maxLength(50)],
    }),
    confirmPassword: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(4), Validators.maxLength(50)],
    }),
  });

  private readonly accountService = inject(AccountService);
  private readonly loginService = inject(LoginService);
  private readonly registerService = inject(RegisterService);
  private readonly translateService = inject(TranslateService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  ngOnInit(): void {
    this.accountService.identity().subscribe(() => {
      if (this.accountService.isAuthenticated()) {
        this.router.navigate(['']);
      }
    });

    // Ouvre directement l'onglet Inscription si on arrive via /login?tab=register
    // (ex : lien "créez-en un" de la page d'accueil).
    const tab = this.route.snapshot.queryParamMap.get('tab');
    if (tab === 'register') {
      this.activeTab.set('register');
    }
  }

  ngAfterViewInit(): void {
    if (this.activeTab() === 'register') {
      this.registerFullNameInput()?.nativeElement.focus();
    } else {
      this.usernameInput()?.nativeElement.focus();
    }
  }

  switchTab(tab: AuthTab): void {
    this.activeTab.set(tab);
    this.authenticationError.set(false);
    this.tooManyAttempts.set(false);
    setTimeout(() => {
      if (tab === 'register') {
        this.registerFullNameInput()?.nativeElement.focus();
      } else {
        this.usernameInput()?.nativeElement.focus();
      }
    });
  }

  login(): void {
    this.authenticationError.set(false);
    this.tooManyAttempts.set(false);

    this.loginService.login(this.loginForm.getRawValue()).subscribe({
      next: account => {
        if (!this.router.currentNavigation()) {
          if (account && this.accountService.hasAnyAuthority([Authority.ADMIN])) {
            this.router.navigate(['/admin']);
          } else {
            this.router.navigate(['']);
          }
        }
      },
      error: (err: HttpErrorResponse) => {
        if (err.status === 429) {
          this.tooManyAttempts.set(true);
        } else {
          this.authenticationError.set(true);
        }
      },
    });
  }

  register(): void {
    this.doNotMatch.set(false);
    this.registerError.set(false);
    this.registerErrorEmailExists.set(false);

    const { password, confirmPassword } = this.registerForm.getRawValue();
    if (password !== confirmPassword) {
      this.doNotMatch.set(true);
      return;
    }

    const { fullName, email } = this.registerForm.getRawValue();
    const normalizedEmail = email.trim().toLowerCase();

    this.registerService
      .save({
        login: normalizedEmail,
        email: normalizedEmail,
        password,
        firstName: fullName.trim(),
        langKey: this.translateService.getCurrentLang(),
      })
      .subscribe({
        next: () => {
          this.registerSuccess.set(true);
          this.registerForm.reset();
          setTimeout(() => {
            this.registerSuccess.set(false);
            this.switchTab('login');
          }, 2500);
        },
        error: response => this.processRegisterError(response),
      });
  }

  private processRegisterError(response: HttpErrorResponse): void {
    if (response.status === 400 && response.error.type === LOGIN_ALREADY_USED_TYPE) {
      this.registerErrorEmailExists.set(true);
    } else if (response.status === 400 && response.error.type === EMAIL_ALREADY_USED_TYPE) {
      this.registerErrorEmailExists.set(true);
    } else {
      this.registerError.set(true);
    }
  }
}
