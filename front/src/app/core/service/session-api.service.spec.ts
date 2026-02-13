import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { Session } from '../models/session.interface';
import { SessionApiService } from './session-api.service';

describe('SessionApiService (integration)', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;

  const session: Session = {
    id: 1,
    name: 'Morning Flow',
    description: 'Breathing and stretching',
    date: new Date('2026-01-15'),
    teacher_id: 2,
    users: [1, 3],
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    service = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should GET all sessions', () => {
    service.all().subscribe((sessions) => {
      expect(sessions).toEqual([session]);
    });

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('GET');
    req.flush([session]);
  });

  it('should GET session detail by id', () => {
    service.detail('1').subscribe((result) => {
      expect(result).toEqual(session);
    });

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('GET');
    req.flush(session);
  });

  it('should DELETE session by id', () => {
    service.delete('1').subscribe();

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should POST a session on create', () => {
    service.create(session).subscribe((result) => {
      expect(result).toEqual(session);
    });

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(session);
    req.flush(session);
  });

  it('should PUT a session on update', () => {
    service.update('1', session).subscribe((result) => {
      expect(result).toEqual(session);
    });

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(session);
    req.flush(session);
  });

  it('should POST participate endpoint', () => {
    service.participate('1', '3').subscribe();

    const req = httpMock.expectOne('api/session/1/participate/3');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toBeNull();
    req.flush(null);
  });

  it('should DELETE unParticipate endpoint', () => {
    service.unParticipate('1', '3').subscribe();

    const req = httpMock.expectOne('api/session/1/participate/3');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
