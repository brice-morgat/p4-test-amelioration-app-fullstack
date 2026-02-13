import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { Teacher } from '../models/teacher.interface';
import { TeacherService } from './teacher.service';

describe('TeacherService (integration)', () => {
  let service: TeacherService;
  let httpMock: HttpTestingController;

  const teacher: Teacher = {
    id: 2,
    firstName: 'Emma',
    lastName: 'Stone',
    createdAt: new Date('2026-01-01'),
    updatedAt: new Date('2026-01-02'),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(TeacherService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should GET all teachers', () => {
    service.all().subscribe((teachers) => {
      expect(teachers).toEqual([teacher]);
    });

    const req = httpMock.expectOne('api/teacher');
    expect(req.request.method).toBe('GET');
    req.flush([teacher]);
  });

  it('should GET teacher detail', () => {
    service.detail('2').subscribe((result) => {
      expect(result).toEqual(teacher);
    });

    const req = httpMock.expectOne('api/teacher/2');
    expect(req.request.method).toBe('GET');
    req.flush(teacher);
  });
});
