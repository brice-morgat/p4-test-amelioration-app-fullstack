import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';

import { Session } from 'src/app/core/models/session.interface';
import { SessionService } from 'src/app/core/service/session.service';
import { SessionApiService } from 'src/app/core/service/session-api.service';
import { TeacherService } from 'src/app/core/service/teacher.service';
import { FormComponent } from './form.component';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;

  const sessionApiServiceMock = {
    detail: jest.fn(),
    create: jest.fn(),
    update: jest.fn(),
  };

  const teacherServiceMock = {
    all: jest.fn(),
  };

  const sessionServiceMock = {
    sessionInformation: {
      admin: true,
      id: 1,
    },
  };

  const snackBarMock = {
    open: jest.fn(),
  };

  const routerMock = {
    url: '/sessions/create',
    navigate: jest.fn(),
  };

  const activatedRouteMock = {
    snapshot: {
      paramMap: {
        get: jest.fn().mockReturnValue('9'),
      },
    },
  };

  const existingSession: Session = {
    id: 9,
    name: 'Evening Flow',
    description: 'Stretch and balance',
    date: new Date('2026-02-01'),
    teacher_id: 2,
    users: [1],
  };

  beforeEach(async () => {
    jest.clearAllMocks();

    teacherServiceMock.all.mockReturnValue(of([]));
    sessionApiServiceMock.detail.mockReturnValue(of(existingSession));
    sessionApiServiceMock.create.mockReturnValue(of(existingSession));
    sessionApiServiceMock.update.mockReturnValue(of(existingSession));

    await TestBed.configureTestingModule({
      imports: [FormComponent],
      providers: [
        { provide: SessionApiService, useValue: sessionApiServiceMock },
        { provide: TeacherService, useValue: teacherServiceMock },
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: MatSnackBar, useValue: snackBarMock },
        { provide: Router, useValue: routerMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
      ],
    }).compileComponents();
  });

  it('should create', () => {
    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    expect(component).toBeTruthy();
  });

  it('should initialize empty form in create mode', () => {
    routerMock.url = '/sessions/create';

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    expect(component.onUpdate).toBe(false);
    expect(component.sessionForm).toBeTruthy();
    expect(component.sessionForm?.value.name).toBe('');
    expect(sessionApiServiceMock.detail).not.toHaveBeenCalled();
  });

  it('should initialize form with existing data in update mode', () => {
    routerMock.url = '/sessions/update/9';

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    expect(component.onUpdate).toBe(true);
    expect(sessionApiServiceMock.detail).toHaveBeenCalledWith('9');
    expect(component.sessionForm?.value.name).toBe('Evening Flow');
    expect(component.sessionForm?.value.teacher_id).toBe(2);
    expect(component.sessionForm?.value.date).toBe('2026-02-01');
  });

  it('should redirect non-admin users', () => {
    routerMock.url = '/sessions/create';
    sessionServiceMock.sessionInformation.admin = false;

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    expect(routerMock.navigate).toHaveBeenCalledWith(['/sessions']);

    sessionServiceMock.sessionInformation.admin = true;
  });

  it('should create session on submit in create mode', () => {
    routerMock.url = '/sessions/create';

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    component.sessionForm?.setValue({
      name: 'Morning Yoga',
      date: '2026-03-02',
      teacher_id: 3,
      description: 'Core strength',
    });

    component.submit();

    expect(sessionApiServiceMock.create).toHaveBeenCalledWith({
      name: 'Morning Yoga',
      date: '2026-03-02',
      teacher_id: 3,
      description: 'Core strength',
    });
    expect(routerMock.navigate).toHaveBeenCalledWith(['sessions']);
  });

  it('should update session on submit in update mode', () => {
    routerMock.url = '/sessions/update/9';

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    component.sessionForm?.setValue({
      name: 'Updated Yoga',
      date: '2026-03-03',
      teacher_id: 4,
      description: 'Breathing',
    });

    component.submit();

    expect(sessionApiServiceMock.update).toHaveBeenCalledWith('9', {
      name: 'Updated Yoga',
      date: '2026-03-03',
      teacher_id: 4,
      description: 'Breathing',
    });
    expect(routerMock.navigate).toHaveBeenCalledWith(['sessions']);
  });
});
