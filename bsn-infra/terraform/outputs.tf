output "ecr_backend_repository_url" {
  value = aws_ecr_repository.backend.repository_url
}

output "ecr_frontend_repository_url" {
  value = aws_ecr_repository.frontend.repository_url
}

output "rds_endpoint" {
  description = "Connection endpoint for the RDS PostgreSQL database (host:port)"
  value       = aws_db_instance.main.endpoint
}

output "rds_address" {
  description = "Hostname for the RDS database"
  value       = aws_db_instance.main.address
}

output "eks_cluster_name" {
  description = "Name of the EKS cluster"
  value       = aws_eks_cluster.main.name
}

output "eks_cluster_endpoint" {
  description = "Kubernetes API endpoint for the EKS cluster"
  value       = aws_eks_cluster.main.endpoint
}

output "configure_kubectl" {
  description = "Command to configure kubectl to connect to this EKS cluster"
  value       = "aws eks update-kubeconfig --region ${var.aws_region} --name ${aws_eks_cluster.main.name} --profile bsn"
}

output "vpc_id" {
  description = "ID of the VPC"
  value       = aws_vpc.main.id
}

output "s3_bucket_name" {
  description = "Name of the S3 bucket for book covers"
  value       = aws_s3_bucket.book_covers.id
}

output "backend_s3_role_arn" {
  description = "ARN of the IAM Role for backend IRSA"
  value       = aws_iam_role.backend_s3.arn
}



