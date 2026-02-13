import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { User } from '../models/user.interface';
import { UserService } from './user.service';

describe('UserService (integration)', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  const user: User = {
    id: 1,
    email: 'john@mail.com',
    firstName: 'John',
    lastName: 'Doe',
    admin: false,
    password: 'secret',
    createdAt: new Date('2026-01-01'),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should GET user by id', () => {
    service.getById('1').subscribe((result) => {
      expect(result).toEqual(user);
    });

    const req = httpMock.expectOne('api/user/1');
    expect(req.request.method).toBe('GET');
    req.flush(user);
  });

  it('should DELETE user by id', () => {
    service.delete('1').subscribe();

    const req = httpMock.expectOne('api/user/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
