import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { LoginRequest } from '../models/loginRequest.interface';
import { RegisterRequest } from '../models/registerRequest.interface';
import { SessionInformation } from '../models/sessionInformation.interface';
import { AuthService } from './auth.service';

describe('AuthService (integration)', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should POST register payload to /api/auth/register', () => {
    const payload: RegisterRequest = {
      email: 'john.doe@mail.com',
      firstName: 'John',
      lastName: 'Doe',
      password: 'secret123',
    };

    service.register(payload).subscribe();

    const request = httpMock.expectOne('/api/auth/register');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(payload);

    request.flush(null);
  });

  it('should POST login payload to /api/auth/login and return session information', () => {
    const payload: LoginRequest = {
      email: 'john.doe@mail.com',
      password: 'secret123',
    };

    const expectedResponse: SessionInformation = {
      token: 'jwt-token',
      type: 'Bearer',
      id: 1,
      username: 'john.doe@mail.com',
      firstName: 'John',
      lastName: 'Doe',
      admin: false,
    };

    let actualResponse: SessionInformation | undefined;

    service.login(payload).subscribe((response) => {
      actualResponse = response;
    });

    const request = httpMock.expectOne('/api/auth/login');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(payload);

    request.flush(expectedResponse);

    expect(actualResponse).toEqual(expectedResponse);
  });
});
