import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { AuthService } from '../../core/service/auth.service';
import { RegisterComponent } from './register.component';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;

  const authServiceMock = {
    register: jest.fn(),
  };

  const routerMock = {
    navigate: jest.fn(),
  };

  beforeEach(async () => {
    jest.clearAllMocks();

    await TestBed.configureTestingModule({
      imports: [RegisterComponent],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should register and navigate on success', () => {
    authServiceMock.register.mockReturnValue(of(void 0));
    component.form.setValue({
      email: 'john@mail.com',
      firstName: 'John',
      lastName: 'Doe',
      password: 'secret123',
    });

    component.submit();

    expect(authServiceMock.register).toHaveBeenCalledWith({
      email: 'john@mail.com',
      firstName: 'John',
      lastName: 'Doe',
      password: 'secret123',
    });
    expect(routerMock.navigate).toHaveBeenCalledWith(['/login']);
    expect(component.onError).toBe(false);
  });

  it('should set onError when register fails', () => {
    authServiceMock.register.mockReturnValue(throwError(() => new Error('register failed')));
    component.form.setValue({
      email: 'john@mail.com',
      firstName: 'John',
      lastName: 'Doe',
      password: 'secret123',
    });

    component.submit();

    expect(component.onError).toBe(true);
  });
});
