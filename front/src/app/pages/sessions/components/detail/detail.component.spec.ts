import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';

import { Session } from 'src/app/core/models/session.interface';
import { Teacher } from 'src/app/core/models/teacher.interface';
import { SessionApiService } from 'src/app/core/service/session-api.service';
import { SessionService } from 'src/app/core/service/session.service';
import { TeacherService } from 'src/app/core/service/teacher.service';
import { DetailComponent } from './detail.component';

describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;

  const sessionApiServiceMock = {
    detail: jest.fn(),
    delete: jest.fn(),
    participate: jest.fn(),
    unParticipate: jest.fn(),
  };

  const teacherServiceMock = {
    detail: jest.fn(),
  };

  const sessionServiceMock = {
    sessionInformation: {
      admin: false,
      id: 1,
    },
  };

  const snackBarMock = {
    open: jest.fn(),
  };

  const routerMock = {
    navigate: jest.fn(),
  };

  const activatedRouteMock = {
    snapshot: {
      paramMap: {
        get: jest.fn().mockReturnValue('5'),
      },
    },
  };

  const session: Session = {
    id: 5,
    name: 'Noon Session',
    description: 'Flexibility class',
    date: new Date('2026-04-01'),
    teacher_id: 2,
    users: [1, 3],
  };

  const teacher: Teacher = {
    id: 2,
    firstName: 'Emma',
    lastName: 'Stone',
    createdAt: new Date('2026-01-01'),
    updatedAt: new Date('2026-01-02'),
  };

  beforeEach(async () => {
    jest.clearAllMocks();

    sessionApiServiceMock.detail.mockReturnValue(of(session));
    sessionApiServiceMock.delete.mockReturnValue(of(void 0));
    sessionApiServiceMock.participate.mockReturnValue(of(void 0));
    sessionApiServiceMock.unParticipate.mockReturnValue(of(void 0));
    teacherServiceMock.detail.mockReturnValue(of(teacher));

    await TestBed.configureTestingModule({
      imports: [DetailComponent],
      providers: [
        { provide: SessionApiService, useValue: sessionApiServiceMock },
        { provide: TeacherService, useValue: teacherServiceMock },
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: MatSnackBar, useValue: snackBarMock },
        { provide: Router, useValue: routerMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should fetch session and teacher on init', () => {
    fixture.detectChanges();

    expect(sessionApiServiceMock.detail).toHaveBeenCalledWith('5');
    expect(teacherServiceMock.detail).toHaveBeenCalledWith('2');
    expect(component.session).toEqual(session);
    expect(component.teacher).toEqual(teacher);
    expect(component.isParticipate).toBe(true);
  });

  it('should set isParticipate to false when user is not in attendees', () => {
    sessionApiServiceMock.detail.mockReturnValue(
      of({ ...session, users: [2, 3] })
    );

    fixture.detectChanges();

    expect(component.isParticipate).toBe(false);
  });

  it('should delete session then show snackbar and navigate', () => {
    fixture.detectChanges();

    component.delete();

    expect(sessionApiServiceMock.delete).toHaveBeenCalledWith('5');
    expect(routerMock.navigate).toHaveBeenCalledWith(['sessions']);
  });

  it('should participate and refetch session', () => {
    const fetchSpy = jest.spyOn(component as any, 'fetchSession').mockImplementation(() => undefined);

    component.participate();

    expect(sessionApiServiceMock.participate).toHaveBeenCalledWith('5', '1');
    expect(fetchSpy).toHaveBeenCalled();
  });

  it('should unParticipate and refetch session', () => {
    const fetchSpy = jest.spyOn(component as any, 'fetchSession').mockImplementation(() => undefined);

    component.unParticipate();

    expect(sessionApiServiceMock.unParticipate).toHaveBeenCalledWith('5', '1');
    expect(fetchSpy).toHaveBeenCalled();
  });

  it('should go back', () => {
    const backSpy = jest.spyOn(window.history, 'back').mockImplementation(() => {});

    component.back();

    expect(backSpy).toHaveBeenCalled();
    backSpy.mockRestore();
  });
});
