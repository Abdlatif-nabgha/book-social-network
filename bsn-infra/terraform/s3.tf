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
          "s3:DeleteObject"
        ]
        Resource = "${aws_s3_bucket.book_covers.arn}/*"
      }
    ]
  })
}
