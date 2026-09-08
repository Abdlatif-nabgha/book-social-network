data "aws_caller_identity" "current" {}

resource "aws_s3_bucket" "book_covers" {
  bucket        = "${var.project_name}-book-covers-${data.aws_caller_identity.current.account_id}"
  force_destroy = true

  tags = {
    Name = "${var.project_name}-book-covers"
  }
}

resource "aws_s3_bucket_server_side_encryption_configuration" "book_covers" {
  bucket = aws_s3_bucket.book_covers.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_s3_bucket_public_access_block" "book_covers" {
  bucket = aws_s3_bucket.book_covers.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

# Attach S3 read/write permissions to EKS Worker Nodes IAM Role
resource "aws_iam_role_policy" "eks_nodes_s3" {
  name = "${var.project_name}-eks-nodes-s3-policy"
  role = aws_iam_role.nodes.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "s3:PutObject",
          "s3:GetObject",
          "s3:DeleteObject",
          "s3:ListBucket"
        ]
        Resource = [
          aws_s3_bucket.book_covers.arn,
          "${aws_s3_bucket.book_covers.arn}/*"
        ]
      }
    ]
  })
}

# ============================================
# IAM Role for Backend Pods (IRSA)
# ============================================
resource "aws_iam_role" "backend_s3" {
  name = "${var.project_name}-backend-s3-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Federated = aws_iam_openid_connect_provider.eks.arn
        }
        Action = "sts:AssumeRoleWithWebIdentity"
        Condition = {
          StringEquals = {
            "${replace(aws_iam_openid_connect_provider.eks.url, "https://", "")}:sub" = "system:serviceaccount:bsn:bsn-backend-sa"
            "${replace(aws_iam_openid_connect_provider.eks.url, "https://", "")}:aud" = "sts.amazonaws.com"
          }
        }
      }
    ]
  })

  tags = {
    Name = "${var.project_name}-backend-s3-role"
  }
}

resource "aws_iam_role_policy" "backend_s3" {
  name = "${var.project_name}-backend-s3-policy"
  role = aws_iam_role.backend_s3.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "s3:PutObject",
          "s3:GetObject",
          "s3:DeleteObject",
          "s3:ListBucket"
        ]
        Resource = [
          aws_s3_bucket.book_covers.arn,
          "${aws_s3_bucket.book_covers.arn}/*"
        ]
      }
    ]
  })
}
