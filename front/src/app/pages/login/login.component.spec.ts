import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { SessionInformation } from 'src/app/core/models/sessionInformation.interface';
import { AuthService } from 'src/app/core/service/auth.service';
import { SessionService } from 'src/app/core/service/session.service';
import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  const authServiceMock = {
    login: jest.fn(),
  };

  const sessionServiceMock = {
    logIn: jest.fn(),
  };

  const routerMock = {
    navigate: jest.fn(),
  };

  const sessionInformation: SessionInformation = {
    token: 'jwt-token',
    type: 'Bearer',
    id: 1,
    username: 'john@mail.com',
    firstName: 'John',
    lastName: 'Doe',
    admin: false,
  };

  beforeEach(async () => {
    jest.clearAllMocks();

    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: Router, useValue: routerMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should login and navigate on submit success', () => {
    authServiceMock.login.mockReturnValue(of(sessionInformation));
    component.form.setValue({ email: 'john@mail.com', password: 'secret123' });

    component.submit();

    expect(authServiceMock.login).toHaveBeenCalledWith({ email: 'john@mail.com', password: 'secret123' });
    expect(sessionServiceMock.logIn).toHaveBeenCalledWith(sessionInformation);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/sessions']);
    expect(component.onError).toBe(false);
  });

  it('should set onError on submit error', () => {
    authServiceMock.login.mockReturnValue(throwError(() => new Error('login failed')));
    component.form.setValue({ email: 'john@mail.com', password: 'secret123' });

    component.submit();

    expect(component.onError).toBe(true);
  });
});
