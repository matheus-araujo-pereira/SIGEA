-- ==============================================================================
-- SIGEA: Sistema Inteligente de Gestão de Eventos Adversos
-- Flyway Migration V3: Módulo da Escala de Usabilidade do Sistema (SUS)
-- ==============================================================================

CREATE TABLE IF NOT EXISTS sus_evaluations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    student_id UUID NOT NULL,
    academic_class_id UUID,
    q1 INT NOT NULL CHECK (q1 BETWEEN 1 AND 5),
    q2 INT NOT NULL CHECK (q2 BETWEEN 1 AND 5),
    q3 INT NOT NULL CHECK (q3 BETWEEN 1 AND 5),
    q4 INT NOT NULL CHECK (q4 BETWEEN 1 AND 5),
    q5 INT NOT NULL CHECK (q5 BETWEEN 1 AND 5),
    q6 INT NOT NULL CHECK (q6 BETWEEN 1 AND 5),
    q7 INT NOT NULL CHECK (q7 BETWEEN 1 AND 5),
    q8 INT NOT NULL CHECK (q8 BETWEEN 1 AND 5),
    q9 INT NOT NULL CHECK (q9 BETWEEN 1 AND 5),
    q10 INT NOT NULL CHECK (q10 BETWEEN 1 AND 5),
    score NUMERIC(5,2) NOT NULL CHECK (score >= 0 AND score <= 100),
    adjective_rating VARCHAR(50) NOT NULL,
    acceptability VARCHAR(50) NOT NULL,
    grade_level VARCHAR(10) NOT NULL,
    suggestions TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sus_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_sus_academic_class FOREIGN KEY (academic_class_id) REFERENCES academic_classes(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_sus_student ON sus_evaluations(student_id);
CREATE INDEX IF NOT EXISTS idx_sus_class ON sus_evaluations(academic_class_id);
CREATE UNIQUE INDEX IF NOT EXISTS uq_sus_student_class ON sus_evaluations(student_id, academic_class_id) WHERE academic_class_id IS NOT NULL;
