import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { of } from 'rxjs';

import { User } from 'src/app/core/models/user.interface';
import { SessionService } from 'src/app/core/service/session.service';
import { UserService } from 'src/app/core/service/user.service';
import { MeComponent } from './me.component';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;

  const sessionServiceMock = {
    sessionInformation: {
      admin: true,
      id: 1,
    },
    logOut: jest.fn(),
  };

  const userServiceMock = {
    getById: jest.fn(),
    delete: jest.fn(),
  };

  const snackBarMock = {
    open: jest.fn(),
  };

  const routerMock = {
    navigate: jest.fn(),
  };

  const user: User = {
    id: 1,
    email: 'john@mail.com',
    firstName: 'John',
    lastName: 'Doe',
    admin: false,
    password: 'secret123',
    createdAt: new Date('2026-01-01'),
  };

  beforeEach(async () => {
    jest.clearAllMocks();
    userServiceMock.getById.mockReturnValue(of(user));
    userServiceMock.delete.mockReturnValue(of(void 0));

    await TestBed.configureTestingModule({
      imports: [MeComponent],
      providers: [
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: UserService, useValue: userServiceMock },
        { provide: MatSnackBar, useValue: snackBarMock },
        { provide: Router, useValue: routerMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should load user on init', () => {
    fixture.detectChanges();

    expect(userServiceMock.getById).toHaveBeenCalledWith('1');
    expect(component.user).toEqual(user);
  });

  it('should delete account and logout', () => {
    fixture.detectChanges();

    component.delete();

    expect(userServiceMock.delete).toHaveBeenCalledWith('1');
    expect(sessionServiceMock.logOut).toHaveBeenCalled();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should go back', () => {
    const backSpy = jest.spyOn(window.history, 'back').mockImplementation(() => {});

    component.back();

    expect(backSpy).toHaveBeenCalled();
    backSpy.mockRestore();
  });
});
